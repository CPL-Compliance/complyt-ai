package com.example.complyt.config.web_clients;

import com.complyt.business.sales_tax_web_clients.FastTaxGetBestMatchWebClientWrapper;
import com.complyt.business.sales_tax_web_clients.StubFastTaxWebClientWrapper;
import com.complyt.business.sales_tax_web_clients.TaxJarWebClientWrapper;
import com.complyt.business.sales_tax_web_clients.ZipTaxWebClientWrapper;
import com.complyt.config.web_clients.SalesTaxWebClientWrapperConfig;
import com.complyt.config.web_clients.WebClientWrapperProperties;
import org.javatuples.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SalesTaxWebClientWrapperConfigTest {

    @InjectMocks
    SalesTaxWebClientWrapperConfig salesTaxWebClientWrapperConfig;

    @Mock
    WebClient webClient;

    @Mock
    WebClientWrapperProperties zipTaxWebClientWrapperProperties;

    @Mock
    WebClientWrapperProperties fastTaxGetBestMatchWebClientWrapperProperties;

    @Test
    void zipTaxWebClientWrapper_CreateInstance_ReturnInstance() {
        when(zipTaxWebClientWrapperProperties.getScheme()).thenReturn("scheme");
        when(zipTaxWebClientWrapperProperties.getHost()).thenReturn("host");
        when(zipTaxWebClientWrapperProperties.getPath()).thenReturn("path");
        when(zipTaxWebClientWrapperProperties.getKey()).thenReturn(new Pair<>("key", "test-value"));

        ZipTaxWebClientWrapper expectedZipTaxWebClientWrapper = new ZipTaxWebClientWrapper(webClient,
                zipTaxWebClientWrapperProperties.getScheme(),
                zipTaxWebClientWrapperProperties.getHost(),
                zipTaxWebClientWrapperProperties.getPath(),
                zipTaxWebClientWrapperProperties.getKey());

        ZipTaxWebClientWrapper actualZipTaxWebClientWrapper =
                salesTaxWebClientWrapperConfig.zipTaxWebClientWrapper(webClient);

        assertEquals(expectedZipTaxWebClientWrapper, actualZipTaxWebClientWrapper);
    }

    @Test
    void fastTaxWebClientWrapper_SetInstance_ReturnInstance() {
        when(fastTaxGetBestMatchWebClientWrapperProperties.getScheme()).thenReturn("scheme");
        when(fastTaxGetBestMatchWebClientWrapperProperties.getHost()).thenReturn("host");
        when(fastTaxGetBestMatchWebClientWrapperProperties.getPath()).thenReturn("path");
        when(fastTaxGetBestMatchWebClientWrapperProperties.getKey()).thenReturn(new Pair<>("key", "test-value"));


        FastTaxGetBestMatchWebClientWrapper expectedFastTaxGetBestMatchWebClientWrapper = new FastTaxGetBestMatchWebClientWrapper(webClient,
                fastTaxGetBestMatchWebClientWrapperProperties.getScheme(),
                fastTaxGetBestMatchWebClientWrapperProperties.getScheme(),
                fastTaxGetBestMatchWebClientWrapperProperties.getPath(),
                fastTaxGetBestMatchWebClientWrapperProperties.getKey());

        FastTaxGetBestMatchWebClientWrapper actualFastTaxGetBestMatchWebClientWrapper =
                salesTaxWebClientWrapperConfig.fastTaxWebClientWrapper(webClient);

        assertEquals(expectedFastTaxGetBestMatchWebClientWrapper, actualFastTaxGetBestMatchWebClientWrapper);
    }

    @Test
    void taxJarWebClientWrapper_SetInstance_ReturnInstance() {
        String apiToken = "api-token";

        TaxJarWebClientWrapper taxJarWebClientWrapper =
                salesTaxWebClientWrapperConfig.taxJarWebClientWrapper(apiToken);

        assertEquals(taxJarWebClientWrapper.getClass(), TaxJarWebClientWrapper.class);
    }

    @Test
    void stubFastTaxWebClientWrapper_SetInstance_ReturnInstance() {
        StubFastTaxWebClientWrapper expectedStubFastTaxWebClientWrapper = new StubFastTaxWebClientWrapper();

        StubFastTaxWebClientWrapper actualStubFastTaxWebClientWrapper =
                salesTaxWebClientWrapperConfig.stubFastTaxWebClientWrapper(webClient);

        assertEquals(expectedStubFastTaxWebClientWrapper, actualStubFastTaxWebClientWrapper);
    }
}
