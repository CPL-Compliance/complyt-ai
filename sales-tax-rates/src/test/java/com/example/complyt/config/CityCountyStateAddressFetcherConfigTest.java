package com.example.complyt.config;

import com.complyt.business.data_fetcher.FastTaxGetBestMatchCityCountyStateAddressFetcher;
import com.complyt.business.data_fetcher.TaxJarCityCountyStateAddressFetcher;
import com.complyt.business.data_fetcher.ZipTaxCityCountyStateAddressFetcher;
import com.complyt.config.CityCountyStateAddressFetcherConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CityCountyStateAddressFetcherConfigTest {

    CityCountyStateAddressFetcherConfig cityCountyStateAddressFetcherConfig;

    @BeforeEach
    void setup() {
        cityCountyStateAddressFetcherConfig = new CityCountyStateAddressFetcherConfig();
    }

    @Test
    void fastTaxCityCountyStateAddressFetcher_SalesTaxWebClientWrapper_ReturnedTransactionFastTaxCityCountyStateAddressFetcher() {
        // Given
        FastTaxGetBestMatchCityCountyStateAddressFetcher expectedFastTaxGetBestMatchCityCountyStateAddressFetcher = new FastTaxGetBestMatchCityCountyStateAddressFetcher();

        // When
        FastTaxGetBestMatchCityCountyStateAddressFetcher actualFastTaxGetBestMatchCityCountyStateAddressFetcher = cityCountyStateAddressFetcherConfig.fastTaxAddressFetcher();

        // Then
        assertEquals(expectedFastTaxGetBestMatchCityCountyStateAddressFetcher, actualFastTaxGetBestMatchCityCountyStateAddressFetcher);
    }

    @Test
    void zipTaxCityCountyStateAddressFetcher_SalesTaxWebClientWrapper_ReturnedTransactionZipTaxCityCountyStateAddressFetcher() {
        // Given
        ZipTaxCityCountyStateAddressFetcher expectedZipTaxCityCountyStateAddressFetcher = new ZipTaxCityCountyStateAddressFetcher();

        // When
        ZipTaxCityCountyStateAddressFetcher actualZipTaxCityCountyStateAddressFetcher = cityCountyStateAddressFetcherConfig.zipTaxAddressFetcher();

        // Then
        assertEquals(expectedZipTaxCityCountyStateAddressFetcher, actualZipTaxCityCountyStateAddressFetcher);
    }

    @Test
    void taxJarCityCountyStateAddressFetcher_SalesTaxWebClientWrapper_ReturnedTransactionZipTaxCityCountyStateAddressFetcher() {
        // Given
        TaxJarCityCountyStateAddressFetcher expectedTaxJarCityCountyStateAddressFetcher = new TaxJarCityCountyStateAddressFetcher();

        // When
        TaxJarCityCountyStateAddressFetcher actualTaxJarCityCountyStateAddressFetcher = cityCountyStateAddressFetcherConfig.taxJarCityCountyStateAddressFetcher();

        // Then
        assertEquals(expectedTaxJarCityCountyStateAddressFetcher, actualTaxJarCityCountyStateAddressFetcher);
    }

}
