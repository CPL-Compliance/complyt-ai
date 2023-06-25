package com.example.complyt.config;

import com.complyt.business.data_fetcher.FastTaxCountyFetcher;
import com.complyt.business.data_fetcher.TaxJarCountyFetcher;
import com.complyt.business.data_fetcher.ZipTaxCountyFetcher;
import com.complyt.config.CountyFetcherConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CountyFetcherConfigTest {

    CountyFetcherConfig countyFetcherConfig;

    @BeforeEach
    void setup() {
        countyFetcherConfig = new CountyFetcherConfig();
    }

    @Test
    void fastTaxCountyFetcher_SalesTaxWebClientWrapper_ReturnedTransactionFastTaxCountyFetcher() {
        // Given
        FastTaxCountyFetcher expectedFastTaxCountyFetcher = new FastTaxCountyFetcher();

        // When
        FastTaxCountyFetcher actualFastTaxCountyFetcher = countyFetcherConfig.fastTaxAddressFetcher();

        // Then
        assertEquals(expectedFastTaxCountyFetcher, actualFastTaxCountyFetcher);
    }

    @Test
    void zipTaxCountyFetcher_SalesTaxWebClientWrapper_ReturnedTransactionZipTaxCountyFetcher() {
        // Given
        ZipTaxCountyFetcher expectedZipTaxCountyFetcher = new ZipTaxCountyFetcher();

        // When
        ZipTaxCountyFetcher actualZipTaxCountyFetcher = countyFetcherConfig.zipTaxAddressFetcher();

        // Then
        assertEquals(expectedZipTaxCountyFetcher, actualZipTaxCountyFetcher);
    }

    @Test
    void taxJarCountyFetcher_SalesTaxWebClientWrapper_ReturnedTransactionZipTaxCountyFetcher() {
        // Given
        TaxJarCountyFetcher expectedTaxJarCountyFetcher = new TaxJarCountyFetcher();

        // When
        TaxJarCountyFetcher actualTaxJarCountyFetcher = countyFetcherConfig.taxJarCountyFetcher();

        // Then
        assertEquals(expectedTaxJarCountyFetcher, actualTaxJarCountyFetcher);
    }

}
