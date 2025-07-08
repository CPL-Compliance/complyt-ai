package io.complyt.services;

import io.complyt.business.address.UsaAbbreviations;
import io.complyt.business.address_aligner.AddressAligner;
import io.complyt.business.address_checkers.HereAddressChecker;
import io.complyt.business.webclients.addressvalidations.HereAddressValidationClientWrapper;
import io.complyt.domain.Address;
import io.complyt.domain.AddressData;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.ValidatedAddress;
import io.complyt.domain.here.HereAddress;
import io.complyt.domain.here.HereAddressData;
import io.complyt.domain.here.HereAddressItem;
import io.complyt.domain.mappers.HereAddressToAddressMapper;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private ValidatedAddress globalValidatedAddress;
    private CachedAddressData cachedAddressData;
    private HereAddressData hereAddressData;
    private CachedAddressData globalCachedAddressData;
    private Address globalAddress;

    @BeforeEach
    void setUp() {
        validAddressService = new ValidAddressServiceImpl(validationAddressRepositoryImpl, hereAddressValidationClientWrapper, hereAddressChecker, addressAligner);
        address = TestUtilities.getAddress();
        cachedAddressData = TestUtilities.getCachedAddressData().withScoring(TestUtilities.getScoring());
        validatedAddress = TestUtilities.getValidatedAddress();
        globalValidatedAddress = TestUtilities.getGlobalValidatedAddress();
        hereAddressData = TestUtilities.getHereAddressData();
        globalCachedAddressData = TestUtilities.getGlobalCachedAddressData();
        globalAddress = TestUtilities.getGlobalAddress();
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
    void validateAddress_AddressNotFoundInRepository_ValidAddress_FindsAndSavesNewAddress() {
        // Arrange
        Address alignedAddress = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);

        when(addressAligner.alignGlobalAddress(eq(address))).thenReturn(alignedAddress);
        when(addressAligner.alignForOutsource(eq(alignedAddress))).thenReturn(alignedAddress);
        when(validationAddressRepositoryImpl.findAddress(eq(alignedAddress))).thenReturn(Mono.empty());
        when(hereAddressValidationClientWrapper.validateAddress(eq(alignedAddress)))
                .thenReturn(Mono.just(hereAddressData));
        when(hereAddressChecker.filterValidAddresses(List.of(cachedAddressData)))
                .thenReturn(Mono.just(List.of(cachedAddressData)));
        when(hereAddressChecker.isCountryAndStateMatch(eq(cachedAddressData), eq(alignedAddress)))
                .thenReturn(true);
        when(validationAddressRepositoryImpl.saveAddress(any()))
                .thenReturn(Mono.just(validatedAddress));

        // Then
        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNext(validatedAddress)
                .verifyComplete();
    }


    @Test
    void validateAddress_AddressNotFoundInRepository_NotValid_NotSavingToDB() {
        Address alignedAddress = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);

        when(addressAligner.alignGlobalAddress(address)).thenReturn(alignedAddress);
        when(validationAddressRepositoryImpl.findAddress(alignedAddress)).thenReturn(Mono.empty());
        when(addressAligner.alignForOutsource(alignedAddress)).thenReturn(alignedAddress);
        when(hereAddressValidationClientWrapper.validateAddress(alignedAddress)).thenReturn(Mono.just(hereAddressData));
        when(hereAddressChecker.filterValidAddresses(List.of(cachedAddressData))).thenReturn(Mono.just(List.of(cachedAddressData)));
        when(hereAddressChecker.isCountryAndStateMatch(cachedAddressData, alignedAddress)).thenReturn(false);

        ValidatedAddress expectedAddress = new ValidatedAddress(null, List.of(cachedAddressData), alignedAddress, LocalDateTime.now());

        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNextMatches(actual ->
                        actual.getId() == null &&
                                actual.getMatchedAddresses().equals(expectedAddress.getMatchedAddresses()) &&
                                actual.getRequestAddress().equals(expectedAddress.getRequestAddress())
                )
                .verifyComplete();
    }


    @Test
    void validateAddress_AddressFoundInRepository_FindsAndSavesNewAddress() {
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.just(validatedAddress));
        ;
        when(addressAligner.alignGlobalAddress(address)).thenReturn(address);

        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    void validateAddress_AddressNotFoundAndCachedAddressHasNoStreetWithBadScore_ValidAddress_ReturnsUpdatedAddressWithGoodScore() {
        // Given
        double badScore = 0.1;

        Address alignedAddress = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);
        Address alignedAddressWithNoStreet = alignedAddress.withStreet(null);
        HereAddress hereAddressNoStreet = TestUtilities.getHereAddressItem().address().withStreet(null);

        CachedAddressData cachedAddressDataNoStreetBadScore =
                cachedAddressData.withAddress(cachedAddressData.address().withStreet(null))
                        .withScoring(cachedAddressData.scoring().withScore(badScore));

        CachedAddressData cachedAddressDataNoStreetGoodScore =
                cachedAddressData.withAddress(cachedAddressData.address().withStreet(null));

        HereAddressData hereAddressDataNoStreetBadScore =
                hereAddressData.withItems(List.of(
                        hereAddressData.getItems().get(0)
                                .withAddress(hereAddressNoStreet)
                                .withScoring(TestUtilities.getHereScoring().withQueryScore(badScore))
                ));

        HereAddressData hereAddressDataNoStreetGoodScore =
                hereAddressData.withItems(List.of(
                        hereAddressData.getItems().get(0)
                                .withAddress(hereAddressNoStreet)
                ));

        when(addressAligner.alignGlobalAddress(eq(address))).thenReturn(alignedAddress);
        when(validationAddressRepositoryImpl.findAddress(eq(alignedAddress))).thenReturn(Mono.empty());
        when(addressAligner.alignForOutsource(eq(alignedAddress))).thenReturn(alignedAddress);
        when(hereAddressValidationClientWrapper.validateAddress(eq(alignedAddress)))
                .thenReturn(Mono.just(hereAddressDataNoStreetBadScore));
        when(hereAddressChecker.filterValidAddresses(eq(List.of(cachedAddressDataNoStreetBadScore))))
                .thenReturn(Mono.empty());

        // Second fallback call
        when(addressAligner.alignForOutsource(eq(alignedAddressWithNoStreet))).thenReturn(alignedAddressWithNoStreet);
        when(hereAddressValidationClientWrapper.validateAddress(eq(alignedAddressWithNoStreet)))
                .thenReturn(Mono.just(hereAddressDataNoStreetGoodScore));
        when(hereAddressChecker.filterValidAddresses(eq(List.of(cachedAddressDataNoStreetGoodScore))))
                .thenReturn(Mono.just(List.of(cachedAddressDataNoStreetGoodScore)));

        // Address check and DB fallback
        when(hereAddressChecker.isCountryAndStateMatch(eq(cachedAddressDataNoStreetGoodScore), eq(alignedAddress)))
                .thenReturn(true);
        when(validationAddressRepositoryImpl.findAddress(argThat(addr ->
                Objects.equals(addr.city(), alignedAddress.city()) &&
                        Objects.equals(addr.country(), alignedAddress.country()) &&
                        Objects.equals(addr.state(), alignedAddress.state()) &&
                        Objects.equals(addr.zip(), alignedAddress.zip()) &&
                        addr.street() == null
        ))).thenReturn(Mono.empty());

        when(validationAddressRepositoryImpl.saveAddress(any())).thenReturn(Mono.just(validatedAddress));

        // Then
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
        CachedAddressData cachedDataWithoutStreet = TestUtilities.getCachedAddressData()
                .withAddress(TestUtilities.getCachedAddressData().address().withStreet(null));

        Address addressWithUsCountry = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);
        Address addressWithNoStreet = addressWithUsCountry.withStreet(null);
        Address alignedAddress = addressWithUsCountry.withStreet(null);

        // Access the private method using reflection
        Method method = ValidAddressServiceImpl.class.getDeclaredMethod("resolveStreetIfMissing", List.class, Address.class);
        method.setAccessible(true);

        when(addressAligner.alignForOutsource(eq(addressWithNoStreet)))
                .thenReturn(alignedAddress);
        when(hereAddressValidationClientWrapper.validateAddress(eq(alignedAddress)))
                .thenReturn(Mono.just(hereAddressData));
        when(validationAddressRepositoryImpl.findAddress(argThat(actual ->
                actual.street() == null &&
                        actual.city().equals(address.city()) &&
                        actual.zip().equals(address.zip()) &&
                        actual.state().equals(address.state()) &&
                        actual.country().equals(UsaAbbreviations.DEFAULT_COUNTRY)
        ))).thenReturn(Mono.empty());

        // Invoke the method
        Mono<List<CachedAddressData>> result =
                (Mono<List<CachedAddressData>>) method.invoke(validAddressService, List.of(cachedDataWithoutStreet), addressWithUsCountry);

        // Then
        StepVerifier.create(result)
                .expectNext(List.of(cachedAddressData))
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

    @Test
    void resolveCountryWithoutZip_UsaAddress_ShouldReturnWithoutChange() throws Exception {
        // Given: both cached and request street are null
        cachedAddressData = cachedAddressData.withAddress(cachedAddressData.address().withStreet(null));
        address = address.withStreet(null);

        // Access private method using reflection
        Method method = ValidAddressServiceImpl.class.getDeclaredMethod("resolveCountryWithoutZip", List.class, Address.class);
        method.setAccessible(true);

        // When
        Mono<List<CachedAddressData>> result = (Mono<List<CachedAddressData>>) method.invoke(validAddressService, List.of(cachedAddressData), address);

        // Then
        StepVerifier.create(result)
                .expectNext(List.of(cachedAddressData))
                .verifyComplete();
    }

    @Test
    void resolveCountryWithoutZip_CachedZipNull_RequestZipNull_ShouldReturnCachedData() throws Exception {
        // Given: both cached and request street are null
        globalCachedAddressData = globalCachedAddressData.withAddress(globalCachedAddressData.address().withZip(null));
        globalAddress = globalAddress.withZip(null);

        // Access private method using reflection
        Method method = ValidAddressServiceImpl.class.getDeclaredMethod("resolveCountryWithoutZip", List.class, Address.class);
        method.setAccessible(true);

        // When
        Mono<List<CachedAddressData>> result = (Mono<List<CachedAddressData>>) method.invoke(validAddressService, List.of(globalCachedAddressData), globalAddress);

        // Then
        StepVerifier.create(result)
                .expectNext(List.of(globalCachedAddressData))
                .verifyComplete();
    }

    @Test
    void resolveCountryWithoutZip_CachedZipNotNull_ShouldReturnCachedData() throws Exception {
        // Given: cached street is not null
        globalCachedAddressData = globalCachedAddressData.withAddress(globalCachedAddressData.address().withZip("12345"));

        // Access private method using reflection
        Method method = ValidAddressServiceImpl.class.getDeclaredMethod("resolveCountryWithoutZip", List.class, Address.class);
        method.setAccessible(true);

        // When
        Mono<List<CachedAddressData>> result = (Mono<List<CachedAddressData>>) method.invoke(validAddressService, List.of(globalCachedAddressData), globalAddress);

        // Then
        StepVerifier.create(result)
                .expectNext(List.of(globalCachedAddressData))
                .verifyComplete();
    }

    @Test
    void validateAddress_NonUsaCountryWithZipMismatch_TriesClearingZipAndValidatingAgain() {
        Address nonUsaAddress = globalAddress.withCountry("GB").withZip("12345");

        when(validationAddressRepositoryImpl.findAddress(nonUsaAddress)).thenReturn(Mono.empty());
        when(addressAligner.alignGlobalAddress(nonUsaAddress)).thenReturn(nonUsaAddress);
        when(addressAligner.alignForOutsource(nonUsaAddress)).thenReturn(nonUsaAddress);

        // First call with zip returns addressData with a different country
        when(hereAddressValidationClientWrapper.validateAddress(nonUsaAddress)).thenReturn(Mono.just(hereAddressData));
        when(hereAddressChecker.filterValidAddresses(List.of(cachedAddressData))).thenReturn(Mono.empty());

        // Second call without zip should be triggered
        when(hereAddressChecker.filterValidAddresses(List.of(cachedAddressData))).thenReturn(Mono.just(List.of(cachedAddressData)));
        when(hereAddressChecker.isCountryAndStateMatch(cachedAddressData, nonUsaAddress)).thenReturn(true);
        when(validationAddressRepositoryImpl.saveAddress(any())).thenReturn(Mono.just(validatedAddress));

        StepVerifier.create(validAddressService.validateAddress(nonUsaAddress))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    void validateAddress_NonUsaCountryWithCorrectZipAndCountry_MatchesAndSavesWithoutZipClearing() {
        Address nonUsaAddress = globalAddress.withCountry("FR").withZip("75001");

        when(validationAddressRepositoryImpl.findAddress(nonUsaAddress)).thenReturn(Mono.empty());
        when(addressAligner.alignGlobalAddress(nonUsaAddress)).thenReturn(nonUsaAddress);
        when(addressAligner.alignForOutsource(nonUsaAddress)).thenReturn(nonUsaAddress);

        when(hereAddressValidationClientWrapper.validateAddress(nonUsaAddress)).thenReturn(Mono.just(hereAddressData));
        when(hereAddressChecker.filterValidAddresses(List.of(cachedAddressData))).thenReturn(Mono.just(List.of(cachedAddressData)));
        when(hereAddressChecker.isCountryAndStateMatch(cachedAddressData, nonUsaAddress)).thenReturn(true);
        when(validationAddressRepositoryImpl.saveAddress(any())).thenReturn(Mono.just(validatedAddress));

        StepVerifier.create(validAddressService.validateAddress(nonUsaAddress))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    void validateAddress_NonUsaCountryInvalidZip_StillNoMatchAfterZipClearing() {
        Address nonUsaAddress = globalAddress.withCountry("DE").withZip("BADZIP");

        when(validationAddressRepositoryImpl.findAddress(nonUsaAddress)).thenReturn(Mono.empty());
        when(addressAligner.alignGlobalAddress(nonUsaAddress)).thenReturn(nonUsaAddress);
        when(addressAligner.alignForOutsource(nonUsaAddress)).thenReturn(nonUsaAddress);

        when(hereAddressValidationClientWrapper.validateAddress(nonUsaAddress)).thenReturn(Mono.just(hereAddressData));
        when(hereAddressChecker.filterValidAddresses(List.of(cachedAddressData))).thenReturn(Mono.empty());

        when(hereAddressChecker.filterValidAddresses(List.of(cachedAddressData))).thenReturn(Mono.just(List.of(cachedAddressData)));
        when(hereAddressChecker.isCountryAndStateMatch(cachedAddressData, nonUsaAddress)).thenReturn(false);

        StepVerifier.create(validAddressService.validateAddress(nonUsaAddress))
                .expectNextMatches(result ->
                        result.getId() == null &&
                                result.getMatchedAddresses().equals(List.of(cachedAddressData)) &&
                                result.getRequestAddress().equals(nonUsaAddress)
                )
                .verifyComplete();
    }

    @Test
    void validateAddress_NonUsa_ZipSame_CountryDifferent_ShouldRetryWithoutZip() {
        Address nonUsaAddress = globalAddress.withCountry("FR").withZip("75001");

        when(validationAddressRepositoryImpl.findAddress(nonUsaAddress)).thenReturn(Mono.empty());
        when(addressAligner.alignGlobalAddress(nonUsaAddress)).thenReturn(nonUsaAddress);
        when(addressAligner.alignForOutsource(nonUsaAddress)).thenReturn(nonUsaAddress);

        // First attempt: no valid addresses
        when(hereAddressValidationClientWrapper.validateAddress(nonUsaAddress)).thenReturn(Mono.just(hereAddressData));
        when(hereAddressChecker.filterValidAddresses(List.of(cachedAddressData))).thenReturn(Mono.empty());

        // Return this mismatched data from resolveBestMatchAddress (via filterValidAddresses -> empty -> resolvePartialAddress)
        when(hereAddressChecker.filterValidAddresses(List.of(cachedAddressData))).thenReturn(Mono.just(List.of(cachedAddressData)));
        when(hereAddressChecker.isCountryAndStateMatch(cachedAddressData, nonUsaAddress)).thenReturn(true);
        when(validationAddressRepositoryImpl.saveAddress(any())).thenReturn(Mono.just(validatedAddress));

        StepVerifier.create(validAddressService.validateAddress(nonUsaAddress))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    void validateAddress_NonUsa_ZipAndCountrySame_ShouldNotRetryWithoutZip() {
        Address nonUsaAddress = globalAddress.withCountry("DE").withZip("10115");

        when(validationAddressRepositoryImpl.findAddress(nonUsaAddress)).thenReturn(Mono.empty());
        when(addressAligner.alignGlobalAddress(nonUsaAddress)).thenReturn(nonUsaAddress);
        when(addressAligner.alignForOutsource(nonUsaAddress)).thenReturn(nonUsaAddress);

        // First call returns data
        when(hereAddressValidationClientWrapper.validateAddress(nonUsaAddress)).thenReturn(Mono.just(hereAddressData));

        // No need for retry: address zip/country match the input
        when(hereAddressChecker.filterValidAddresses(List.of(cachedAddressData))).thenReturn(Mono.just(List.of(cachedAddressData)));

        when(hereAddressChecker.isCountryAndStateMatch(cachedAddressData, nonUsaAddress)).thenReturn(true);
        when(validationAddressRepositoryImpl.saveAddress(any())).thenReturn(Mono.just(validatedAddress));

        StepVerifier.create(validAddressService.validateAddress(nonUsaAddress))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    void resolveFallbackAddress_UsaStreetMissingInBestMatch_ShouldTriggerStreetFallback() {
        Address input = address.withCountry("USA").withStreet("Main St");
        Address expected = input.withStreet(null);

        HereAddressItem hereAddress = TestUtilities.getHereAddressItem();
        HereAddressData hereData = new HereAddressData(List.of(hereAddress));
        List<CachedAddressData> fallbackList = HereAddressToAddressMapper.INSTANCE.map(hereData);

        when(addressAligner.alignForOutsource(expected)).thenReturn(expected);
        when(hereAddressValidationClientWrapper.validateAddress(expected)).thenReturn(Mono.just(hereData));
        when(validationAddressRepositoryImpl.findAddress(expected)).thenReturn(Mono.empty());

        CachedAddressData cached = cachedAddressData.withAddress(address.withStreet(null));
        List<CachedAddressData> inputList = List.of(cached);

        StepVerifier.create(validAddressService.resolveFallbackAddress(inputList, input))
                .expectNext(fallbackList)
                .verifyComplete();
    }

    @Test
    void resolveFallbackAddress_UsaStreetPresentInCached_ShouldReturnOriginalList() {
        Address input = address.withCountry("USA").withStreet("Main St");

        CachedAddressData cached = cachedAddressData.withAddress(address.withStreet("Main St"));
        List<CachedAddressData> inputList = List.of(cached);

        StepVerifier.create(validAddressService.resolveFallbackAddress(inputList, input))
                .expectNext(inputList)
                .verifyComplete();
    }

    @Test
    void resolveFallbackAddress_NonUsa_ZipSame_CountryDifferent_ShouldTriggerZipFallback() {
        Address input = globalAddress.withCountry("CA").withZip("12345");
        Address fallback = input.withZip(null);

        HereAddressItem hereAddress = TestUtilities.getHereAddressItem();
        HereAddressData hereData = new HereAddressData(List.of(hereAddress));
        List<CachedAddressData> fallbackList = HereAddressToAddressMapper.INSTANCE.map(hereData);

        when(addressAligner.alignForOutsource(fallback)).thenReturn(fallback);
        when(hereAddressValidationClientWrapper.validateAddress(fallback)).thenReturn(Mono.just(hereData));
        when(validationAddressRepositoryImpl.findAddress(fallback)).thenReturn(Mono.empty());

        CachedAddressData cached = globalCachedAddressData.withAddress(
                globalCachedAddressData.address().withCountry("FR").withZip("12345")
        );
        List<CachedAddressData> inputList = List.of(cached);

        StepVerifier.create(validAddressService.resolveFallbackAddress(inputList, input))
                .expectNext(fallbackList)
                .verifyComplete();
    }

    @Test
    void resolveFallbackAddress_NonUsa_ZipAndCountrySame_ShouldReturnOriginalList() {
        Address input = globalAddress.withCountry("FR").withZip("75001");

        CachedAddressData cached = globalCachedAddressData.withAddress(
                globalCachedAddressData.address().withCountry("FR").withZip("75001")
        );
        List<CachedAddressData> inputList = List.of(cached);

        StepVerifier.create(validAddressService.resolveFallbackAddress(inputList, input))
                .expectNext(inputList)
                .verifyComplete();
    }

    @Test
    void resolveFallbackAddress_NonUsa_EmptyMatch_ShouldTriggerSwitchIfEmpty() {
        Address input = globalAddress.withCountry("DE").withZip("BADZIP");
        Address fallback = input.withZip(null);

        HereAddressItem hereAddress = TestUtilities.getHereAddressItem();
        HereAddressData hereData = new HereAddressData(List.of(hereAddress));
        List<CachedAddressData> fallbackList = HereAddressToAddressMapper.INSTANCE.map(hereData);

        when(addressAligner.alignForOutsource(fallback)).thenReturn(fallback);
        when(hereAddressValidationClientWrapper.validateAddress(fallback)).thenReturn(Mono.just(hereData));
        when(validationAddressRepositoryImpl.findAddress(fallback)).thenReturn(Mono.empty());

        StepVerifier.create(validAddressService.resolveFallbackAddress(List.of(), input))
                .expectNext(fallbackList)
                .verifyComplete();
    }

    @Test
    void resolveSecondFallbackAddress_UsaCountry_ShouldReturnOriginalList() {
        Address input = address.withCountry("USA");

        List<CachedAddressData> addressDataList = List.of(cachedAddressData);

        StepVerifier.create(validAddressService.resolveSecondFallbackAddress(addressDataList, input))
                .expectNext(addressDataList)
                .verifyComplete();
    }

    @Test
    void resolveSecondFallbackAddress_NonUsa_CountryMismatch_ShouldCallFindByAddressClientWrapper() {
        // Given
        Address input = globalAddress.withCountry("FR"); // Requesting FR
        Address expectedMapped = Address.builder().country("FR").build(); // Address to fetch

        CachedAddressData cached = globalCachedAddressData.withAddress(
                globalCachedAddressData.address().withCountry("CA")
        );
        List<CachedAddressData> inputList = List.of(cached);

        HereAddressData hereData = TestUtilities.getHereAddressData();
        List<CachedAddressData> mappedList = HereAddressToAddressMapper.INSTANCE.map(hereData);

        when(validationAddressRepositoryImpl.findAddress(expectedMapped)).thenReturn(Mono.empty());
        when(addressAligner.alignForOutsource(expectedMapped)).thenReturn(expectedMapped);
        when(hereAddressValidationClientWrapper.validateAddress(expectedMapped)).thenReturn(Mono.just(hereData));

        // When
        StepVerifier.create(validAddressService.resolveSecondFallbackAddress(inputList, input))
                .expectNext(mappedList)
                .verifyComplete();
    }

}