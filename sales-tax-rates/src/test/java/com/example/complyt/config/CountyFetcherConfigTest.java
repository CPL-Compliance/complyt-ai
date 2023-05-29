package com.example.complyt.config;

import com.complyt.business.data_fetcher.FastTaxAddressFetcher;
import com.complyt.business.data_fetcher.ZipTaxAddressFetcher;
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
        FastTaxAddressFetcher expectedFastTaxCountyFetcher = new FastTaxAddressFetcher();

        // When
        FastTaxAddressFetcher actualFastTaxCountyFetcher = countyFetcherConfig.FastTaxCountyFetcher();

        // Then
        assertEquals(expectedFastTaxCountyFetcher, actualFastTaxCountyFetcher);
    }

    @Test
    void transactionZipTaxCountyFetcher_SalesTaxWebClientWrapper_ReturnedTransactionZipTaxCountyFetcher() {
        // Given
        ZipTaxAddressFetcher expectedZipTaxCountyFetcher = new ZipTaxAddressFetcher();

        // When
        ZipTaxAddressFetcher actualZipTaxCountyFetcher = countyFetcherConfig.ZipTaxCountyFetcher();

        // Then
        assertEquals(expectedZipTaxCountyFetcher, actualZipTaxCountyFetcher);
    }

}
