package com.complyt.config;

import com.complyt.business.sales_tax.sales_tax_web_clients.ZipTaxWebClientWrapper;
import com.complyt.business.utils.data_fetcher.TransactionZipTaxCountyFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CountyFetcherConfigTest {

    CountyFetcherConfig countyFetcherConfig;

    @MockBean
    ZipTaxWebClientWrapper zipTaxWebClientWrapper;

    @MockBean
    WebClient webClient;

    @BeforeEach
    void setUp() {
        countyFetcherConfig = new CountyFetcherConfig(zipTaxWebClientWrapper);

    }

    @Test
    void initZipTaxCountyFetcher_CountyFetcherCreated_CountyFetcherReturned() {
        // Given
        TransactionZipTaxCountyFetcher expectedFetcher = new TransactionZipTaxCountyFetcher(zipTaxWebClientWrapper);;

        // When + Then
        TransactionZipTaxCountyFetcher actualFetcher = countyFetcherConfig.transactionZipTaxCountyFetcher(webClient);
        assertEquals(expectedFetcher,actualFetcher);
    }

}
