package com.complyt.config;

import com.complyt.business.data_fetcher.TransactionFastTaxCountyFetcher;
import com.complyt.business.data_fetcher.TransactionZipTaxCountyFetcher;
import com.complyt.business.sales_tax.sales_tax_web_clients.FastTaxWebClientWrapper;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.business.sales_tax.sales_tax_web_clients.StubFastTaxWebClientWrapper;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

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
        TransactionFastTaxCountyFetcher transactionFastTaxCountyFetcher = new TransactionFastTaxCountyFetcher(salesTaxWebClientWrapper);
        // When
        TransactionFastTaxCountyFetcher newTransactionFastTaxCountyFetcher = countyFetcherConfig.transactionFastTaxCountyFetcher(salesTaxWebClientWrapper);
        // Then
        assertEquals(transactionFastTaxCountyFetcher, newTransactionFastTaxCountyFetcher);
    }

    @Test
    void transactionZipTaxCountyFetcher_SalesTaxWebClientWrapper_ReturnedTransactionZipTaxCountyFetcher() {
        // Given
        salesTaxWebClientWrapper = new StubFastTaxWebClientWrapper(); // any salesTaxWebClientWrapper
        TransactionZipTaxCountyFetcher transactionZipTaxCountyFetcher = new TransactionZipTaxCountyFetcher(salesTaxWebClientWrapper);
        // When
        TransactionZipTaxCountyFetcher newTransactionZipTaxCountyFetcher = countyFetcherConfig.transactionZipTaxCountyFetcher(salesTaxWebClientWrapper);
        // Then
        assertEquals(transactionZipTaxCountyFetcher, newTransactionZipTaxCountyFetcher);
    }

}
