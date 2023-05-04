package com.example.complyt.config;

import com.complyt.business.data_fetcher.FastTaxCountyFetcher;
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
    void transactionFastTaxCountyFetcher_SalesTaxWebClientWrapper_ReturnedTransactionFastTaxCountyFetcher() {
        // Given
        FastTaxCountyFetcher expectedFastTaxCountyFetcher = new FastTaxCountyFetcher();

        // When
        FastTaxCountyFetcher actualFastTaxCountyFetcher = countyFetcherConfig.FastTaxCountyFetcher();

        // Then
        assertEquals(expectedFastTaxCountyFetcher, actualFastTaxCountyFetcher);
    }

    @Test
    void transactionZipTaxCountyFetcher_SalesTaxWebClientWrapper_ReturnedTransactionZipTaxCountyFetcher() {
        // Given
        ZipTaxCountyFetcher expectedZipTaxCountyFetcher = new ZipTaxCountyFetcher();

        // When
        ZipTaxCountyFetcher actualZipTaxCountyFetcher = countyFetcherConfig.ZipTaxCountyFetcher();

        // Then
        assertEquals(expectedZipTaxCountyFetcher, actualZipTaxCountyFetcher);
    }

}
