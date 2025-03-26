package io.complyt.services;

import io.complyt.business.address.UsaAbbreviations;
import io.complyt.business.address_aligner.AddressAligner;
import io.complyt.business.address_checkers.HereAddressChecker;
import io.complyt.business.webclients.addressvalidations.HereAddressValidationClientWrapper;
import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.ValidatedAddress;
import io.complyt.domain.here.HereAddress;
import io.complyt.domain.here.HereAddressData;
import io.complyt.repositories.ValidationAddressRepositoryImpl;
import io.complyt.utils.exceptions.types.ObjectNotValidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.TestUtilities;

import java.lang.reflect.Method;
import java.util.Collections;
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
    private HereAddressChecker hereAddressChecker;

    @Mock
    private AddressAligner addressAligner;

    private Address address;
    private ValidatedAddress validatedAddress;
    private CachedAddressData cachedAddressData;
    private HereAddressData hereAddressData;

    @BeforeEach
    void setUp() {
        validAddressService = new ValidAddressServiceImpl(validationAddressRepositoryImpl, hereAddressValidationClientWrapper, hereAddressChecker, addressAligner);
        address = TestUtilities.getAddress();
        cachedAddressData = TestUtilities.getCachedAddressData().withScoring(TestUtilities.getScoring());
        validatedAddress = TestUtilities.getValidatedAddress();
        hereAddressData = TestUtilities.getHereAddressData();
    }

    @Test
    void validateAddress_AddressFoundInRepository_ReturnsMappedAddress() {
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.just(validatedAddress));
        when(addressAligner.alignGlobalAddress(address)).thenReturn(address);

        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    void validateAddress_AddressNotFoundInRepository_FindsAndSavesNewAddress() {
        Address alignedAddress = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.empty());
        when(addressAligner.alignForOutsource(address)).thenReturn(alignedAddress);
        when(hereAddressValidationClientWrapper.validateAddress(alignedAddress)).thenReturn(Mono.just(hereAddressData));
        when(hereAddressChecker.filterValidAddresses(List.of(cachedAddressData))).thenReturn(Mono.just(List.of(cachedAddressData)));
        when(validationAddressRepositoryImpl.saveAddress(any())).thenReturn(Mono.just(validatedAddress));
        when(addressAligner.alignGlobalAddress(address)).thenReturn(address);

        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    void validateAddress_AddressFoundInRepository_FindsAndSavesNewAddress() {
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.just(validatedAddress));;
        when(addressAligner.alignGlobalAddress(address)).thenReturn(address);

        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    void validateAddress_AddressNotFoundAndCachedAddressHasNoStreetWithBadScore_ReturnsUpdatedAddressWithGoodScore() {
        // Given
        double badScore = 0.1;
        Address alignedAddress = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);
        Address alignedAddressWithNoStreet = alignedAddress.withStreet(null);
        HereAddress hereAddressNoStreet = TestUtilities.getHereAddressItem().address().withStreet(null);
        CachedAddressData cachedAddressDataNoStreetBadScore = cachedAddressData.withAddress(cachedAddressData.address().withStreet(null)).withScoring(cachedAddressData.scoring().withScore(badScore));
        CachedAddressData cachedAddressDataNoStreetGoodScore = cachedAddressData.withAddress(cachedAddressData.address().withStreet(null));
        HereAddressData hereAddressDataNoStreetBadScore = hereAddressData.withItems(List.of(hereAddressData.getItems().get(0).withAddress(hereAddressNoStreet).withScoring(TestUtilities.getHereScoring().withQueryScore(badScore))));
        HereAddressData hereAddressDataNoStreetGoodScore = hereAddressData.withItems(List.of(hereAddressData.getItems().get(0).withAddress(hereAddressNoStreet)));


        // When
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.empty());
        when(addressAligner.alignForOutsource(address)).thenReturn(alignedAddress);
        when(hereAddressValidationClientWrapper.validateAddress(alignedAddress)).thenReturn(Mono.just(hereAddressDataNoStreetBadScore));
        when(hereAddressChecker.filterValidAddresses(List.of(cachedAddressDataNoStreetBadScore))).thenReturn(Mono.empty());
        when(addressAligner.alignForOutsource(address.withStreet(null))).thenReturn(alignedAddress.withStreet(null));
        when(hereAddressValidationClientWrapper.validateAddress(alignedAddressWithNoStreet)).thenReturn(Mono.just(hereAddressDataNoStreetGoodScore));
        when(hereAddressChecker.filterValidAddresses(List.of(cachedAddressDataNoStreetGoodScore))).thenReturn(Mono.just(List.of(cachedAddressDataNoStreetGoodScore)));

        when(validationAddressRepositoryImpl.saveAddress(any())).thenReturn(Mono.just(validatedAddress));
        when(addressAligner.alignGlobalAddress(address)).thenReturn(address);

        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNext(validatedAddress)
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
        CachedAddressData cachedDataWithoutStreet = TestUtilities.getCachedAddressData().withAddress(TestUtilities.getCachedAddressData().address().withStreet(null));
        address = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);
        Address alignedAddress = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);

        // Access the private method using reflection
        Method method = ValidAddressServiceImpl.class.getDeclaredMethod("resolveStreetIfMissing", List.class, Address.class);
        method.setAccessible(true);

        when(addressAligner.alignForOutsource(address.withStreet(null))).thenReturn(alignedAddress.withStreet(null));
        when(hereAddressValidationClientWrapper.validateAddress(address.withStreet(null))).thenReturn(Mono.just(hereAddressData));

        // Explicitly cast the result to Mono<CachedAddressData> using method return type
        Mono<List<CachedAddressData>> result = Mono.class.cast(method.invoke(validAddressService, List.of(cachedDataWithoutStreet), address));

        // Then
        StepVerifier.create(result)
                .expectNext(Collections.singletonList(cachedAddressData)) // Validate the street is resolved
                .verifyComplete();
    }

    @Test
    void resolveStreetIfMissing_StreetInCachedData_ResolvesSuccessfully() throws Exception {
        // Given
        CachedAddressData cachedDataWithoutStreet = TestUtilities.getCachedAddressData();
        address = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);

        // Access the private method using reflection
        Method method = ValidAddressServiceImpl.class.getDeclaredMethod("resolveStreetIfMissing", List.class, Address.class);
        method.setAccessible(true);

        // Explicitly cast the result to Mono<CachedAddressData> using method return type
        Mono<List<CachedAddressData>> result = Mono.class.cast(method.invoke(validAddressService, List.of(cachedDataWithoutStreet), address));

        // Then
        StepVerifier.create(result)
                .expectNext(Collections.singletonList(cachedDataWithoutStreet))
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

    @Test
    void resolveAddress_AddressFoundInRepository_ReturnsValidatedAddress() {
        cachedAddressData = validatedAddress.getMatchedAddresses().get(0);
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.just(validatedAddress));
        when(hereAddressChecker.validateCountryAndStateMatch(cachedAddressData, address)).thenReturn(Mono.just(cachedAddressData));
        when(addressAligner.alignGlobalAddress(address)).thenReturn(address);

        Mono<CachedAddressData> result = validAddressService.resolveAddress(address);

        StepVerifier.create(result)
                .expectNext(cachedAddressData)
                .verifyComplete();
    }

    @Test
    void resolveAddress_AddressFoundInRepository_StateNotMatch_ReturnsValidatedAddress() {
        cachedAddressData = validatedAddress.getMatchedAddresses().get(0);
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.just(validatedAddress));
        when(hereAddressChecker.validateCountryAndStateMatch(cachedAddressData, address)).thenReturn(Mono.error(new ObjectNotValidException("State mismatch")));
        when(addressAligner.alignGlobalAddress(address)).thenReturn(address);

        Mono<CachedAddressData> result = validAddressService.resolveAddress(address);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotValidException &&
                        throwable.getMessage().contains("State mismatch"))
                .verify();
    }

    @Test
    void resolveBestMatchAddress_EmptyList_ReturnsEmptyMono() throws Exception {
        // Given
        List<CachedAddressData> emptyList = List.of();

        // Using reflection to access private method
        Method method = ValidAddressServiceImpl.class.getDeclaredMethod("resolveBestMatchAddress", List.class);
        method.setAccessible(true);

        // When
        Mono<CachedAddressData> result = (Mono<CachedAddressData>) method.invoke(validAddressService, emptyList);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void resolveStreetIfMissing_CachedStreetNull_RequestStreetNull_ShouldReturnCachedData() throws Exception {
        // Given: both cached and request street are null
        cachedAddressData = cachedAddressData.withAddress(cachedAddressData.address().withStreet(null));
        address = address.withStreet(null);

        // Access private method using reflection
        Method method = ValidAddressServiceImpl.class.getDeclaredMethod("resolveStreetIfMissing", List.class, Address.class);
        method.setAccessible(true);

        // When
        Mono<List<CachedAddressData>> result = (Mono<List<CachedAddressData>>) method.invoke(validAddressService, List.of(cachedAddressData), address);

        // Then
        StepVerifier.create(result)
                .expectNext(List.of(cachedAddressData))
                .verifyComplete();
    }

    @Test
    void resolveStreetIfMissing_CachedStreetNotNull_ShouldReturnCachedData() throws Exception {
        // Given: cached street is not null
        cachedAddressData = cachedAddressData.withAddress(cachedAddressData.address().withStreet("5th Ave"));

        // Access private method using reflection
        Method method = ValidAddressServiceImpl.class.getDeclaredMethod("resolveStreetIfMissing", List.class, Address.class);
        method.setAccessible(true);

        // When
        Mono<List<CachedAddressData>> result = (Mono<List<CachedAddressData>>) method.invoke(validAddressService, List.of(cachedAddressData), address);

        // Then
        StepVerifier.create(result)
                .expectNext(List.of(cachedAddressData))
                .verifyComplete();
    }
}