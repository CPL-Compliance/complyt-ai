package com.example.complyt.config;

import com.complyt.business.data_fetcher.FastTaxGetBestMatchCityCountyFetcher;
import com.complyt.business.data_fetcher.FastTaxGetTaxInfoByCityCountyStateCityCountyFetcher;
import com.complyt.business.data_fetcher.TaxJarCityCountyFetcher;
import com.complyt.business.data_fetcher.ZipTaxCityCountyFetcher;
import com.complyt.config.CityCountyFetcherConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CityCountyFetcherConfigTest {

    CityCountyFetcherConfig cityCountyFetcherConfig;

    @BeforeEach
    void setup() {
        cityCountyFetcherConfig = new CityCountyFetcherConfig();
    }

    @Test
    void fastTaxGetBestMatchCityCountyAddressFetcher_SalesTaxWebClientWrapper_ReturnedFastTaxCityCountyAddressFetcher() {
        // Given
        FastTaxGetBestMatchCityCountyFetcher expectedFastTaxGetBestMatchCityCountyAddressFetcher = new FastTaxGetBestMatchCityCountyFetcher();

        // When
        FastTaxGetBestMatchCityCountyFetcher actualFastTaxGetBestMatchCityCountyAddressFetcher = cityCountyFetcherConfig.fastTaxGetBestMatchCityCountyAddressFetcher();

        // Then
        assertEquals(expectedFastTaxGetBestMatchCityCountyAddressFetcher, actualFastTaxGetBestMatchCityCountyAddressFetcher);
    }

    @Test
    void fastTaxGetByCityCountyStateCityCountyFetcher_SalesTaxWebClientWrapper_ReturnedFastTaxGetByCityCountyStateAddressFetcher() {
        // Given
        FastTaxGetTaxInfoByCityCountyStateCityCountyFetcher expectedFastTaxGetBestMatchCityCountyAddressFetcher = new FastTaxGetTaxInfoByCityCountyStateCityCountyFetcher();

        // When
        FastTaxGetTaxInfoByCityCountyStateCityCountyFetcher actualFastTaxGetBestMatchCityCountyAddressFetcher = cityCountyFetcherConfig.fastTaxGetByCityCountyAddressFetcher();

        // Then
        assertEquals(expectedFastTaxGetBestMatchCityCountyAddressFetcher, actualFastTaxGetBestMatchCityCountyAddressFetcher);
    }

    @Test
    void zipTaxCityCountyAddressFetcher_SalesTaxWebClientWrapper_ReturnedTransactionZipTaxCityCountyAddressFetcher() {
        // Given
        ZipTaxCityCountyFetcher expectedZipTaxCityCountyAddressFetcher = new ZipTaxCityCountyFetcher();

        // When
        ZipTaxCityCountyFetcher actualZipTaxCityCountyAddressFetcher = cityCountyFetcherConfig.zipTaxAddressFetcher();

        // Then
        assertEquals(expectedZipTaxCityCountyAddressFetcher, actualZipTaxCityCountyAddressFetcher);
    }

    @Test
    void taxJarCityCountyAddressFetcher_SalesTaxWebClientWrapper_ReturnedTransactionZipTaxCityCountyAddressFetcher() {
        // Given
        TaxJarCityCountyFetcher expectedTaxJarCityCountyAddressFetcher = new TaxJarCityCountyFetcher();

        // When
        TaxJarCityCountyFetcher actualTaxJarCityCountyAddressFetcher = cityCountyFetcherConfig.taxJarCityCountyAddressFetcher();

        // Then
        assertEquals(expectedTaxJarCityCountyAddressFetcher, actualTaxJarCityCountyAddressFetcher);
    }

}
