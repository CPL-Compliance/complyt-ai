package com.example.complyt.config;

import com.complyt.business.data_fetcher.FastTaxCountyFetcher;
import com.complyt.business.data_fetcher.ZipTaxCountyFetcher;
import com.complyt.business.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.business.sales_tax_web_clients.StubFastTaxWebClientWrapper;
import com.complyt.config.CountyFetcherConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CountyFetcherConfigTest {

    @Mock
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;
    CountyFetcherConfig countyFetcherConfig;

    @BeforeEach
    void setup() {
        countyFetcherConfig = new CountyFetcherConfig();
    }

    @Test
    void transactionFastTaxCountyFetcher_SalesTaxWebClientWrapper_ReturnedTransactionFastTaxCountyFetcher() {
        // Given
        salesTaxWebClientWrapper = new StubFastTaxWebClientWrapper(); // any salesTaxWebClientWrapper
        FastTaxCountyFetcher expectedFastTaxCountyFetcher = new FastTaxCountyFetcher(salesTaxWebClientWrapper);

        // When
        FastTaxCountyFetcher actualFastTaxCountyFetcher = countyFetcherConfig.FastTaxCountyFetcher(salesTaxWebClientWrapper);

        // Then
        assertEquals(expectedFastTaxCountyFetcher, actualFastTaxCountyFetcher);
    }

    @Test
    void transactionZipTaxCountyFetcher_SalesTaxWebClientWrapper_ReturnedTransactionZipTaxCountyFetcher() {
        // Given
        salesTaxWebClientWrapper = new StubFastTaxWebClientWrapper(); // any salesTaxWebClientWrapper
        ZipTaxCountyFetcher expectedZipTaxCountyFetcher = new ZipTaxCountyFetcher(salesTaxWebClientWrapper);

        // When
        ZipTaxCountyFetcher actualZipTaxCountyFetcher = countyFetcherConfig.ZipTaxCountyFetcher(salesTaxWebClientWrapper);

        // Then
        assertEquals(expectedZipTaxCountyFetcher, actualZipTaxCountyFetcher);
    }

}
