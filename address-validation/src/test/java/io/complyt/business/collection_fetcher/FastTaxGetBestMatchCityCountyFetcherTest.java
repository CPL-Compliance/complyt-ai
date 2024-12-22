package io.complyt.business.collection_fetcher;

import io.complyt.business.external_fetcher.FastTaxGetBestMatchCityCountyFetcher;
import io.complyt.domain.AddressData;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import io.complyt.domain.fast_tax.TaxInfoItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.TestUtilities;

import java.nio.channels.UnresolvedAddressException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FastTaxGetBestMatchCityCountyFetcherTest {

    @InjectMocks
    private FastTaxGetBestMatchCityCountyFetcher fetcher;

    @BeforeEach
    void setup() {
        fetcher = new FastTaxGetBestMatchCityCountyFetcher();
    }

    @Test
    void fetch_validData_returnsUpdatedCachedAddressData() {
        // Arrange
        FastTaxGetBestMatchData addressData = TestUtilities.createFastTaxGetBestMatchData();
        CachedAddressData cachedAddressData = TestUtilities.getCachedAddressData().withCounty(null);
        TaxInfoItem taxInfoItem = addressData.getTaxInfoItems().get(0);
        CachedAddressData expectedAddressData = cachedAddressData.withCounty(taxInfoItem.county()).withCity(taxInfoItem.city());

        // Act
        Mono<CachedAddressData> result = fetcher.fetch(addressData, cachedAddressData);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedAddressData).verifyComplete();
    }

    @Test
    void fetch_nullTaxInfoItems_throwsUnresolvedAddressException() {
        // Arrange
        FastTaxGetBestMatchData addressData = TestUtilities.createFastTaxGetBestMatchData().withTaxInfoItems(null);
        CachedAddressData cachedAddressData = TestUtilities.getCachedAddressData();

        // Act
        Mono<CachedAddressData> result = fetcher.fetch(addressData, cachedAddressData);

        // Assert
        StepVerifier.create(result)
                .expectError(UnresolvedAddressException.class)
                .verify();
    }

    @Test
    void fetch_NullAddressDataPassed_ThrowsException() {
        // Given
        AddressData addressData = null;
        CachedAddressData cachedAddressData = TestUtilities.getCachedAddressData();

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            fetcher.fetch(addressData, cachedAddressData);
        });

        assertEquals(nullPointerException.getMessage(), "addressData " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void fetch_NullCachedAddressDataPassed_ThrowsException() {
        // Given
        CachedAddressData cachedAddressData = null;
        AddressData addressData = TestUtilities.getHereAddressData();

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            fetcher.fetch(addressData, cachedAddressData);
        });

        assertEquals(nullPointerException.getMessage(), "cachedAddressData " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

}