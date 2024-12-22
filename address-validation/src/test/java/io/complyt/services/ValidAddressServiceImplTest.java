package io.complyt.services;

import io.complyt.business.address.UsaAbbreviations;
import io.complyt.business.address_checkers.HereAddressChecker;
import io.complyt.business.external_fetcher.FastTaxGetBestMatchCityCountyFetcher;
import io.complyt.business.webclients.addressvalidations.FastTaxGetBestMatchWebClientWrapper;
import io.complyt.business.webclients.addressvalidations.HereAddressValidationClientWrapper;
import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.ValidatedAddress;
import io.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import io.complyt.domain.here.HereAddress;
import io.complyt.domain.here.HereAddressData;
import io.complyt.domain.mappers.ValidatedAddressToAddressMapper;
import io.complyt.repositories.ValidationAddressRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.TestUtilities;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidAddressServiceImplTest {

    private ValidAddressServiceImpl validAddressService;

    @Mock
    private ValidationAddressRepositoryImpl validationAddressRepositoryImpl;

    @Mock
    private HereAddressValidationClientWrapper hereAddressValidationClientWrapper;

    @Mock
    private FastTaxGetBestMatchWebClientWrapper fastTaxWebClientWrapper;

    @Mock
    private FastTaxGetBestMatchCityCountyFetcher fastTaxGetBestMatchCityCountyFetcher;

    @Mock
    private HereAddressChecker hereAddressChecker;

    private Address address;
    private ValidatedAddress validatedAddress;
    private CachedAddressData cachedAddressData;
    private HereAddressData hereAddressData;

    @BeforeEach
    void setUp() {
        validAddressService = new ValidAddressServiceImpl(validationAddressRepositoryImpl, hereAddressValidationClientWrapper, fastTaxWebClientWrapper, fastTaxGetBestMatchCityCountyFetcher, hereAddressChecker);
        address = TestUtilities.getAddress();
        cachedAddressData = TestUtilities.getCachedAddressData();
        validatedAddress = TestUtilities.getValidatedAddress();
        hereAddressData = TestUtilities.getHereAddressData();
    }

    @Test
    void validateAddress_AddressFoundInRepository_ReturnsMappedAddress() {
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.just(validatedAddress));

        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNext(ValidatedAddressToAddressMapper.INSTANCE.map(validatedAddress))
                .verifyComplete();
    }

    @Test
    void validateAddress_AddressNotFoundInRepository_FindsAndSavesNewAddress() {
        Address alignedAddress = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.empty());
        when(hereAddressValidationClientWrapper.validateAddress(alignedAddress)).thenReturn(Mono.just(hereAddressData));
        when(hereAddressChecker.checkAddress(cachedAddressData, address)).thenReturn(Mono.just(cachedAddressData));
        when(validationAddressRepositoryImpl.saveAddress(any())).thenReturn(Mono.just(validatedAddress));

        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNext(ValidatedAddressToAddressMapper.INSTANCE.map(validatedAddress))
                .verifyComplete();
    }

    @Test
    void validateAddress_AddressFoundInRepository_FindsAndSavesNewAddress() {
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.just(validatedAddress));;

        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNext(ValidatedAddressToAddressMapper.INSTANCE.map(validatedAddress))
                .verifyComplete();
    }

    @Test
    void validateAddress_AddressNotFound_CachedAddressNoCounty_ReturnsWithCounty() {
        // Given
        Address alignedAddress = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);
        HereAddress hereAddressNoCounty = TestUtilities.getHereAddressItem().address().withCounty(null);
        CachedAddressData cachedAddressDataNoCounty = cachedAddressData.withCounty(null);
        HereAddressData hereAddressDataNoCounty = hereAddressData.withItems(List.of(hereAddressData.getItems().get(0).withAddress(hereAddressNoCounty)));
        FastTaxGetBestMatchData fastTax = TestUtilities.createFastTaxGetBestMatchData();

        // When
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.empty());
        when(hereAddressValidationClientWrapper.validateAddress(alignedAddress)).thenReturn(Mono.just(hereAddressDataNoCounty));
        when(hereAddressChecker.checkAddress(cachedAddressDataNoCounty, address)).thenReturn(Mono.just(cachedAddressDataNoCounty));
        when(fastTaxWebClientWrapper.validateAddress(address)).thenReturn(Mono.just(fastTax));
        when(fastTaxGetBestMatchCityCountyFetcher.fetch(fastTax, cachedAddressDataNoCounty)).thenReturn(Mono.just(cachedAddressData)); // withCounty
        when(validationAddressRepositoryImpl.saveAddress(any())).thenReturn(Mono.just(validatedAddress));

        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNext(ValidatedAddressToAddressMapper.INSTANCE.map(validatedAddress))
                .verifyComplete();
    }

    @Test
    void validateAddress_AddressNotFoundAndCachedAddressHasNoStreetWithBadScore_ReturnsUpdatedAddressWithGoodScore() {
        // Given
        double badScore = 0.1;
        Address alignedAddress = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);
        Address alignedAddressWithNoStreet = alignedAddress.withStreet(null);
        HereAddress hereAddressNoStreet = TestUtilities.getHereAddressItem().address().withStreet(null);
        CachedAddressData cachedAddressDataNoStreetBadScore = cachedAddressData.withStreet(null).withScore(badScore);
        CachedAddressData cachedAddressDataNoStreetGoodScore = cachedAddressData.withStreet(null);
        HereAddressData hereAddressDataNoStreetBadScore = hereAddressData.withItems(List.of(hereAddressData.getItems().get(0).withAddress(hereAddressNoStreet).withScoring(TestUtilities.getHereScoring().withQueryScore(badScore))));
        HereAddressData hereAddressDataNoStreetGoodScore = hereAddressData.withItems(List.of(hereAddressData.getItems().get(0).withAddress(hereAddressNoStreet)));


        // When
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.empty());
        when(hereAddressValidationClientWrapper.validateAddress(alignedAddress)).thenReturn(Mono.just(hereAddressDataNoStreetBadScore));
        when(hereAddressChecker.checkAddress(cachedAddressDataNoStreetBadScore, address)).thenReturn(Mono.empty());
        when(hereAddressValidationClientWrapper.validateAddress(alignedAddressWithNoStreet)).thenReturn(Mono.just(hereAddressDataNoStreetGoodScore));
        when(hereAddressChecker.checkAddress(cachedAddressDataNoStreetGoodScore, address)).thenReturn(Mono.just(cachedAddressDataNoStreetGoodScore));

        when(validationAddressRepositoryImpl.saveAddress(any())).thenReturn(Mono.just(validatedAddress));

        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNext(ValidatedAddressToAddressMapper.INSTANCE.map(validatedAddress))
                .verifyComplete();
    }

    @Test
    void findByAddress_AddressFound_ReturnsValidatedAddress() {
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.just(validatedAddress));

        StepVerifier.create(validAddressService.findByAddress(address))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    void findByAddress_AddressNotFound_ReturnsEmpty() {
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.empty());

        StepVerifier.create(validAddressService.findByAddress(address))
                .verifyComplete();
    }

    @Test
    void saveAddress_ValidAddress_ReturnsSavedValidatedAddress() {
        when(validationAddressRepositoryImpl.saveAddress(validatedAddress)).thenReturn(Mono.just(validatedAddress));

        StepVerifier.create(validAddressService.saveAddress(validatedAddress))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    void resolveStreetIfMissing_NoStreetInCachedData_ResolvesSuccessfully() throws Exception {
        // Given
        CachedAddressData cachedDataWithoutStreet = TestUtilities.getCachedAddressData().withStreet(null);
        address = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);

        // Access the private method using reflection
        Method method = ValidAddressServiceImpl.class.getDeclaredMethod("resolveStreetIfMissing", CachedAddressData.class, Address.class);
        method.setAccessible(true);

        when(hereAddressValidationClientWrapper.validateAddress(address.withStreet(null))).thenReturn(Mono.just(hereAddressData));

        // Explicitly cast the result to Mono<CachedAddressData> using method return type
        Mono<CachedAddressData> result = Mono.class.cast(method.invoke(validAddressService, cachedDataWithoutStreet, address));

        // Then
        StepVerifier.create(result)
                .expectNext(cachedAddressData) // Validate the street is resolved
                .verifyComplete();
    }

    @Test
    void resolveStreetIfMissing_StreetInCachedData_ResolvesSuccessfully() throws Exception {
        // Given
        CachedAddressData cachedDataWithoutStreet = TestUtilities.getCachedAddressData();
        address = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);

        // Access the private method using reflection
        Method method = ValidAddressServiceImpl.class.getDeclaredMethod("resolveStreetIfMissing", CachedAddressData.class, Address.class);
        method.setAccessible(true);

        // Explicitly cast the result to Mono<CachedAddressData> using method return type
        Mono<CachedAddressData> result = Mono.class.cast(method.invoke(validAddressService, cachedDataWithoutStreet, address));

        // Then
        StepVerifier.create(result)
                .expectNext(cachedAddressData)
                .verifyComplete();
    }

    @Test
    void findByAddress_NullAddress_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> validAddressService.findByAddress(null));
    }

    @Test
    void saveAddress_NullValidatedAddress_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> validAddressService.saveAddress(null));
    }

    @Test
    void validateAddress_NullValidatedAddress_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> validAddressService.validateAddress(null));
    }
}