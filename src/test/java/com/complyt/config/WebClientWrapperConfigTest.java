package com.complyt.config;

import com.complyt.business.sales_tax.sales_tax_web_clients.FastTaxWebClientWrapper;
import com.complyt.business.sales_tax.sales_tax_web_clients.ZipTaxWebClientWrapper;
import org.javatuples.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class WebClientWrapperConfigTest {

    @InjectMocks
    WebClientWrapperConfig webClientWrapperConfig;

    @Mock
    WebClient webClient;

    @Test
    void zipTaxWebClientWrapper_SetInstance_ReturnInstance(){
        String scheme = "https";
        String host = "api.zip-tax.com";
        String path = "request/v40";
        Pair<String, String> key = new Pair<>("key", "jkRvcDF9MVB5pxtm");

        ZipTaxWebClientWrapper expectedZipTaxWebClientWrapper =
                new ZipTaxWebClientWrapper(webClient, scheme, host, path, key);

        ZipTaxWebClientWrapper actualZipTaxWebClientWrapper =
                webClientWrapperConfig.zipTaxWebClientWrapper(webClient);

        assertEquals(expectedZipTaxWebClientWrapper,actualZipTaxWebClientWrapper);
    }

    @Test
    void fastTaxWebClientWrapper_SetInstance_ReturnInstance(){
        String scheme = "https";
        String host = "api.zip-tax.com";
        String path = "request/v40";
        Pair<String, String> key = new Pair<>("key", "jkRvcDF9MVB5pxtm");

        FastTaxWebClientWrapper expectedFastTaxWebClientWrapper =
                new FastTaxWebClientWrapper(webClient, scheme, host, path, key);

        FastTaxWebClientWrapper actualFastTaxWebClientWrapper =
                webClientWrapperConfig.fastTaxWebClientWrapper(webClient);

        assertEquals(expectedFastTaxWebClientWrapper,actualFastTaxWebClientWrapper);
    }
}
