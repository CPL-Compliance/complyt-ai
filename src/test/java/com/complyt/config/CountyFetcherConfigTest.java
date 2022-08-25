package com.complyt.config;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.business.utils.data_fetcher.TransactionFastTaxCountyFetcher;
import com.complyt.business.utils.data_fetcher.TransactionZipTaxCountyFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CountyFetcherConfigTest {

    CountyFetcherConfig countyFetcherConfig;

    @MockBean
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @BeforeEach
    void setUp() {
        countyFetcherConfig = new CountyFetcherConfig();
    }

    @Test
    void initZipTaxCountyFetcher_CountyFetcherCreated_CountyFetcherReturned() {
        // Given
        TransactionZipTaxCountyFetcher expectedFetcher = new TransactionZipTaxCountyFetcher(salesTaxWebClientWrapper);
        ;

        // When + Then
        TransactionZipTaxCountyFetcher actualFetcher = countyFetcherConfig.transactionZipTaxCountyFetcher(salesTaxWebClientWrapper);
        assertEquals(expectedFetcher, actualFetcher);
    }

    @Test
    void initFastTaxCountyFetcher_CountyFetcherCreated_CountyFetcherReturned() {
        // Given
        TransactionFastTaxCountyFetcher expectedFetcher = new TransactionFastTaxCountyFetcher(salesTaxWebClientWrapper);
        ;

        // When + Then
        TransactionFastTaxCountyFetcher actualFetcher = countyFetcherConfig.transactionFastTaxCountyFetcher(salesTaxWebClientWrapper);
        assertEquals(expectedFetcher, actualFetcher);
    }
}