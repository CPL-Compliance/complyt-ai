package com.complyt.config;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.business.sales_tax.sales_tax_web_clients.StubFastTaxWebClientWrapper;
import com.complyt.business.transaction.data_fetcher.TransactionFastTaxCountyFetcher;
import com.complyt.business.transaction.data_fetcher.TransactionZipTaxCountyFetcher;
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
        TransactionFastTaxCountyFetcher expectedTransactionFastTaxCountyFetcher = new TransactionFastTaxCountyFetcher(salesTaxWebClientWrapper);
        // When
        TransactionFastTaxCountyFetcher actualTransactionFastTaxCountyFetcher = countyFetcherConfig.transactionFastTaxCountyFetcher(salesTaxWebClientWrapper);
        // Then
        assertEquals(expectedTransactionFastTaxCountyFetcher, actualTransactionFastTaxCountyFetcher);
    }

    @Test
    void transactionZipTaxCountyFetcher_SalesTaxWebClientWrapper_ReturnedTransactionZipTaxCountyFetcher() {
        // Given
        salesTaxWebClientWrapper = new StubFastTaxWebClientWrapper(); // any salesTaxWebClientWrapper
        TransactionZipTaxCountyFetcher expectedTransactionZipTaxCountyFetcher = new TransactionZipTaxCountyFetcher(salesTaxWebClientWrapper);
        // When
        TransactionZipTaxCountyFetcher actualTransactionZipTaxCountyFetcher = countyFetcherConfig.transactionZipTaxCountyFetcher(salesTaxWebClientWrapper);
        // Then
        assertEquals(expectedTransactionZipTaxCountyFetcher, actualTransactionZipTaxCountyFetcher);
    }

}
