package io.complyt.business.address_checkers;

import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.enums.FieldMatchType;
import io.complyt.domain.enums.FieldsMatchScore;
import io.complyt.utils.exceptions.types.ObjectNotValidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.TestUtilities;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HereAddressCheckerTest {

    @InjectMocks
    HereAddressChecker hereAddressChecker;

    private Address address;
    private CachedAddressData cachedAddressData;
    private List<CachedAddressData> cachedAddressDataList;

    @BeforeEach
    void setUp() {
        address = TestUtilities.getAddress();
        cachedAddressData = TestUtilities.getCachedAddressData();
        cachedAddressDataList = List.of(cachedAddressData);
    }

    @Test
    void validateCountryAndStateMatch_StateMatches_ReturnsData() {
        // Given
        cachedAddressData = cachedAddressData.withAddress(address);

        // When
        Mono<CachedAddressData> result = hereAddressChecker.validateCountryAndStateMatch(cachedAddressData, address);

        // Then
        StepVerifier.create(result).expectNext(cachedAddressData).verifyComplete();
    }

    @Test
    void validateCountryAndStateMatch_StateMismatch_ThrowsException() {
        // Given
        FieldsMatchScore fieldsMatchScore = new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.NO_MATCH, FieldMatchType.EXACT, FieldMatchType.EXACT,FieldMatchType.EXACT, null);
        cachedAddressData = cachedAddressData.withScoring(TestUtilities.getScoring().withFieldScore(fieldsMatchScore));
        cachedAddressData = cachedAddressData.withAddress(cachedAddressData.address().withState("DifferentState"));

        // When
        Mono<CachedAddressData> result = hereAddressChecker.validateCountryAndStateMatch(cachedAddressData, address);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotValidException &&
                        throwable.getMessage().contains("ERR-ADDR-002"))
                .verify();
    }

    @Test
    void filterValidAddresses_ValidAddresses_ReturnsFilteredList() {
        // When
        cachedAddressData = cachedAddressData.withScoring(TestUtilities.getScoring().withScore(1));
        Mono<List<CachedAddressData>> result = hereAddressChecker.filterValidAddresses(List.of(cachedAddressData));

        // Then
        StepVerifier.create(result)
                .expectNextMatches(list -> !list.isEmpty() && list.contains(cachedAddressData))
                .verifyComplete();
    }

    @Test
    void filterValidAddresses_InvalidAddresses_ReturnsEmptyList() {
        // Given
        cachedAddressData = cachedAddressData.withScoring(TestUtilities.getScoring().withScore(0.1));
        cachedAddressDataList = List.of(cachedAddressData);

        // When
        Mono<List<CachedAddressData>> result = hereAddressChecker.filterValidAddresses(cachedAddressDataList);

        // Then
        StepVerifier.create(result).expectComplete().verify();
    }

    @Test
    void filterValidAddresses_EmptyList_ReturnsEmptyList() {
        // Given
        List<CachedAddressData> emptyList = List.of();

        // When
        Mono<List<CachedAddressData>> result = hereAddressChecker.filterValidAddresses(emptyList);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void isValidAddress_ValidAddress_ReturnsTrue() {
        // When
        cachedAddressData = cachedAddressData.withScoring(TestUtilities.getScoring().withScore(1));
        boolean result = hereAddressChecker.isValidAddress(cachedAddressData);

        // Then
        assertTrue(result);
    }

    @Test
    void isValidAddress_InvalidAddress_ReturnsFalse() {
        // Given
        CachedAddressData invalidData = cachedAddressData.withScoring(null).withAddress(null);

        // When
        boolean result = hereAddressChecker.isValidAddress(invalidData);

        // Then
        assertFalse(result);
    }

    @Test
    void isValidAddress_NullAddress_ReturnsFalse() {
        // When
        boolean result = hereAddressChecker.isValidAddress(null);

        // Then
        assertFalse(result);
    }

    @Test
    void isValidAddress_ZipNull_ReturnsFalse() {
        // When
        cachedAddressData = cachedAddressData.withAddress(TestUtilities.getAddress().withZip(null));
        boolean result = hereAddressChecker.isValidAddress(cachedAddressData);

        // Then
        assertFalse(result);
    }

    @Test
    void isValidAddress_CountyNull_ReturnsFalse() {
        // When
        cachedAddressData = cachedAddressData.withAddress(TestUtilities.getAddress().withCounty(null));
        boolean result = hereAddressChecker.isValidAddress(cachedAddressData);

        // Then
        assertFalse(result);
    }


    @Test
    void isValidAddress_CountyAndZipNullNonUS_ReturnsTrue() {
        // When
        cachedAddressData = cachedAddressData.withAddress(TestUtilities.getAddress().withCountry("Canada").withCounty(null));
        boolean result = hereAddressChecker.isValidAddress(cachedAddressData);

        // Then
        assertTrue(result);
    }

    @Test
    public void resolveAddress_NullData_ThrowsNullPointerException() {
        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            hereAddressChecker.validateCountryAndStateMatch(null, address).block();
        });

        // Assert
        assertEquals("data is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void resolveAddress_NullAddress_ThrowsNullPointerException() {
        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            hereAddressChecker.validateCountryAndStateMatch(cachedAddressData, null).block();
        });

        // Assert
        assertEquals("requestAddress is marked non-null but is null", exception.getMessage());
    }

    @Test
    void isValidAddress_NullItem_ShouldReturnFalse() {
        // Given
        CachedAddressData item = null;

        // When
        boolean result = hereAddressChecker.isValidAddress(item);

        // Then
        assertFalse(result, "Expected false when item is null");
    }

    @Test
    void isValidAddress_NullScoring_ShouldReturnFalse() {
        // Given
        CachedAddressData item = new CachedAddressData(new Address("New York", "USA", "New York County", "NY", "5th Ave", "10001", null,false), null);

        // When
        boolean result = hereAddressChecker.isValidAddress(item);

        // Then
        assertFalse(result, "Expected false when scoring is null");
    }

    @Test
    void isValidAddress_NullAddress_ShouldReturnFalse() {
        // Given
        CachedAddressData item = new CachedAddressData(null, cachedAddressData.scoring());

        // When
        boolean result = hereAddressChecker.isValidAddress(item);

        // Then
        assertFalse(result, "Expected false when address is null");
    }

    @Test
    void isValidAddress_ValidItem_ShouldReturnTrue() {
        // When
        boolean result = hereAddressChecker.isValidAddress(cachedAddressData);

        // Then
        assertTrue(result, "Expected true when item, address, and scoring are valid");
    }

    @Test
    void validateCountryAndStateMatch_countryMismatch_shouldReturnError() {
        // given
        CachedAddressData dataWithNoCountryMatch = cachedAddressData.withScoring(
                cachedAddressData.scoring().withFieldScore(
                        cachedAddressData.scoring().fieldScore().withCountryMatch(FieldMatchType.NO_MATCH)
                )
        );

        // when & then
        StepVerifier.create(hereAddressChecker.validateCountryAndStateMatch(dataWithNoCountryMatch, address))
                .expectErrorSatisfies(ex -> {
                    assertTrue(ex instanceof ObjectNotValidException);
                    assertTrue(ex.getMessage().contains("The country you provided (US) does not match "));
                })
                .verify();
    }

    @Test
    void validateCountryAndStateMatch_nonUsaCountry_shouldReturnSuccessEvenIfStateMismatch() {
        // given
        Address nonUsAddress = address.withCountry("FR"); // France or any non-USA country
        CachedAddressData dataWithStateMismatch = cachedAddressData.withScoring(
                cachedAddressData.scoring().withFieldScore(
                        cachedAddressData.scoring().fieldScore()
                                .withCountryMatch(FieldMatchType.EXACT)
                                .withStateMatch(FieldMatchType.NO_MATCH)
                )
        );

        // when & then
        StepVerifier.create(hereAddressChecker.validateCountryAndStateMatch(dataWithStateMismatch, nonUsAddress))
                .expectNext(dataWithStateMismatch)
                .verifyComplete();
    }

    @Test
    void validateCountryAndStateMatch_usaCountryStateMismatch_shouldReturnError() {
        // given
        Address usAddress = address.withCountry("US");
        CachedAddressData dataWithStateMismatch = cachedAddressData.withScoring(
                cachedAddressData.scoring().withFieldScore(
                        cachedAddressData.scoring().fieldScore()
                                .withCountryMatch(FieldMatchType.EXACT)
                                .withStateMatch(FieldMatchType.NO_MATCH)
                )
        );

        // when & then
        StepVerifier.create(hereAddressChecker.validateCountryAndStateMatch(dataWithStateMismatch, usAddress))
                .expectErrorSatisfies(ex -> {
                    assertTrue(ex instanceof ObjectNotValidException);
                    assertTrue(ex.getMessage().contains("The state you provided (CA) does not match"));
                })
                .verify();
    }

    @Test
    void validateCountryAndStateMatch_usaCountryStateMatch_shouldReturnSuccess() {
        // given
        Address usAddress = address.withCountry("US");
        CachedAddressData validData = cachedAddressData.withScoring(
                cachedAddressData.scoring().withFieldScore(
                        cachedAddressData.scoring().fieldScore()
                                .withCountryMatch(FieldMatchType.EXACT)
                                .withStateMatch(FieldMatchType.EXACT)
                )
        );

        // when & then
        StepVerifier.create(hereAddressChecker.validateCountryAndStateMatch(validData, usAddress))
                .expectNext(validData)
                .verifyComplete();
    }

    @Test
    void validateCountryAndStateMatch_NotUsaCountryMatchStateMatchNull_shouldReturnError() {
        // given
        Address usAddress = address.withCountry("Germany");
        CachedAddressData dataWithStateMismatch = cachedAddressData.withScoring(
                cachedAddressData.scoring().withFieldScore(
                        cachedAddressData.scoring().fieldScore()
                                .withCountryMatch(FieldMatchType.EXACT)
                                .withStateMatch(null)
                )
        );

        // when & then
        StepVerifier.create(hereAddressChecker.validateCountryAndStateMatch(dataWithStateMismatch, usAddress))
                .expectNext(dataWithStateMismatch).verifyComplete();
    }

    // Shouldn't happen but just in case - If US, has to have the stateMatch
    @Test
    void validateCountryAndStateMatch_usaCountry_stateMatchNull_shouldReturnSuccess() {
        // given
        Address usAddress = address.withCountry("US");
        CachedAddressData dataWithNullStateMatch = cachedAddressData.withScoring(
                cachedAddressData.scoring().withFieldScore(
                        cachedAddressData.scoring().fieldScore()
                                .withCountryMatch(FieldMatchType.EXACT)
                                .withStateMatch(null) // critical case
                )
        );

        // when & then
        StepVerifier.create(hereAddressChecker.validateCountryAndStateMatch(dataWithNullStateMatch, usAddress))
                .expectNext(dataWithNullStateMatch)
                .verifyComplete();
    }
}
