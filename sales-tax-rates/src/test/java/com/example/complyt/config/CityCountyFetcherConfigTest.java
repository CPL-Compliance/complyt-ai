package com.example.complyt.config;

import com.complyt.business.data_fetcher.FastTaxGetBestMatchCityCountyFetcher;
import com.complyt.business.data_fetcher.TaxJarCityCountyFetcher;
import com.complyt.business.data_fetcher.ZipTaxCityCountyFetcher;
import com.complyt.config.CityCountyAddressFetcherConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CityCountyFetcherConfigTest {

    CityCountyAddressFetcherConfig cityCountyAddressFetcherConfig;

    @BeforeEach
    void setup() {
        cityCountyAddressFetcherConfig = new CityCountyAddressFetcherConfig();
    }

    @Test
    void fastTaxCityCountyAddressFetcher_SalesTaxWebClientWrapper_ReturnedTransactionFastTaxCityCountyAddressFetcher() {
        // Given
        FastTaxGetBestMatchCityCountyFetcher expectedFastTaxGetBestMatchCityCountyAddressFetcher = new FastTaxGetBestMatchCityCountyFetcher();

        // When
        FastTaxGetBestMatchCityCountyFetcher actualFastTaxGetBestMatchCityCountyAddressFetcher = cityCountyAddressFetcherConfig.fastTaxGetBestMatchCityCountyAddressFetcher();

        // Then
        assertEquals(expectedFastTaxGetBestMatchCityCountyAddressFetcher, actualFastTaxGetBestMatchCityCountyAddressFetcher);
    }

    @Test
    void zipTaxCityCountyAddressFetcher_SalesTaxWebClientWrapper_ReturnedTransactionZipTaxCityCountyAddressFetcher() {
        // Given
        ZipTaxCityCountyFetcher expectedZipTaxCityCountyAddressFetcher = new ZipTaxCityCountyFetcher();

        // When
        ZipTaxCityCountyFetcher actualZipTaxCityCountyAddressFetcher = cityCountyAddressFetcherConfig.zipTaxAddressFetcher();

        // Then
        assertEquals(expectedZipTaxCityCountyAddressFetcher, actualZipTaxCityCountyAddressFetcher);
    }

    @Test
    void taxJarCityCountyAddressFetcher_SalesTaxWebClientWrapper_ReturnedTransactionZipTaxCityCountyAddressFetcher() {
        // Given
        TaxJarCityCountyFetcher expectedTaxJarCityCountyAddressFetcher = new TaxJarCityCountyFetcher();

        // When
        TaxJarCityCountyFetcher actualTaxJarCityCountyAddressFetcher = cityCountyAddressFetcherConfig.taxJarCityCountyAddressFetcher();

        // Then
        assertEquals(expectedTaxJarCityCountyAddressFetcher, actualTaxJarCityCountyAddressFetcher);
    }

}
