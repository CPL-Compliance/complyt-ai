package com.example.complyt.business.data_fetcher;

import com.complyt.business.data_fetcher.FastTaxGetByCityCountyFetcher;
import com.complyt.domain.CityCountyWrapper;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxGetByCityCountyStateData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FastTaxGetByCityCountyStateCityCountyFetcherTest {

    private FastTaxGetByCityCountyFetcher fastTaxGetByCityCountyFetcher;

    @BeforeEach
    void setUp() {
        fastTaxGetByCityCountyFetcher = new FastTaxGetByCityCountyFetcher();
    }


    @Test
    void fetch_FetchesCityCounty_ReturnsCounty() {
        // Given
        FastTaxGetByCityCountyStateData fastTaxGetByCityCountyStateData = new FastTaxGetByCityCountyStateData("city", "county", "countyFips", "state", "", "", "", "", "", "", "", "");

        CityCountyWrapper cityCountyWrapper = new CityCountyWrapper(fastTaxGetByCityCountyStateData.getCity(), fastTaxGetByCityCountyStateData.getCounty());

        // When
        Mono<CityCountyWrapper> cityCountyWrapperMono = fastTaxGetByCityCountyFetcher.fetch(fastTaxGetByCityCountyStateData);

        // Then
        StepVerifier.create(cityCountyWrapperMono).expectNext(cityCountyWrapper).verifyComplete();
    }

    @Test
    void fetch_NullSalesTaxDataPassed_ThrowsException() {
        // Given
        SalesTaxData nullSalesTaxData = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            fastTaxGetByCityCountyFetcher.fetch(nullSalesTaxData);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxData " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void equals_SameFastTaxCityCountyFetcher_ReturnsTrue() {
        // Given
        FastTaxGetByCityCountyFetcher givenFastTaxCityCountyAddressFetcher = new FastTaxGetByCityCountyFetcher();

        // When
        boolean isEquals = fastTaxGetByCityCountyFetcher.equals(givenFastTaxCityCountyAddressFetcher);

        // Then
        assertTrue(isEquals);
    }
}
