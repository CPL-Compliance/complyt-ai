package com.complyt.config;

import com.complyt.business.sales_tax.sales_tax_web_clients.FastTaxWebClientWrapper;
import com.complyt.business.sales_tax.sales_tax_web_clients.ZipTaxWebClientWrapper;
import com.complyt.config.web_clients.WebClientWrapperProperties;
import com.complyt.config.web_clients.WebClientWrapperConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.javatuples.Pair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class WebClientWrapperConfigTest {

    @InjectMocks
    WebClientWrapperConfig webClientWrapperConfig;

    @Mock
    WebClient webClient;

    @Mock
    WebClientWrapperProperties zipTaxWebClientWrapperProperties;

    @Mock
    WebClientWrapperProperties fastTaxWebClientWrapperProperties;

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
                webClientWrapperConfig.zipTaxWebClientWrapper(webClient);

        assertEquals(expectedZipTaxWebClientWrapper, actualZipTaxWebClientWrapper);
    }

    @Test
    void fastTaxWebClientWrapper_SetInstance_ReturnInstance() {
        when(fastTaxWebClientWrapperProperties.getScheme()).thenReturn("scheme");
        when(fastTaxWebClientWrapperProperties.getHost()).thenReturn("host");
        when(fastTaxWebClientWrapperProperties.getPath()).thenReturn("path");
        when(fastTaxWebClientWrapperProperties.getKey()).thenReturn(new Pair<>("key", "test-value"));


        FastTaxWebClientWrapper expectedFastTaxWebClientWrapper = new FastTaxWebClientWrapper(webClient,
                fastTaxWebClientWrapperProperties.getScheme(),
                fastTaxWebClientWrapperProperties.getScheme(),
                fastTaxWebClientWrapperProperties.getPath(),
                fastTaxWebClientWrapperProperties.getKey());

        FastTaxWebClientWrapper actualFastTaxWebClientWrapper =
                webClientWrapperConfig.fastTaxWebClientWrapper(webClient);

        assertEquals(expectedFastTaxWebClientWrapper, actualFastTaxWebClientWrapper);
    }
}
