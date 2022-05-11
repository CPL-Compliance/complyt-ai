package com.complyt.config;

import com.complyt.business.sales_tax.FastTaxWebClientWrapper;
import com.complyt.business.sales_tax.ZipTaxWebClientWrapper;
import org.javatuples.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class WebClientWrapperConfigTest {

    @InjectMocks
    WebClientWrapperConfig webClientWrapperConfig;

    @Mock
    WebClient zipTaxWebClient;

    @Mock
    WebClient fastTaxWebClient;


    @Test
    void zipTaxWebClientWrapper_SetInstance_ReturnInstance(){
        String scheme = "https";
        String host = "api.zip-tax.com";
        String path = "request/v40";
        Pair<String, String> key = new Pair<>("key", "jkRvcDF9MVB5pxtm");
        RestTemplate restTemplate = new RestTemplate();

        ZipTaxWebClientWrapper expectedZipTaxWebClientWrapper =
                new ZipTaxWebClientWrapper(restTemplate,zipTaxWebClient, scheme, host, path, key);

        ZipTaxWebClientWrapper actualZipTaxWebClientWrapper =
                webClientWrapperConfig.zipTaxWebClientWrapper(zipTaxWebClient);

        assertEquals(expectedZipTaxWebClientWrapper,actualZipTaxWebClientWrapper);
    }

    @Test
    void fastTaxWebClientWrapper_SetInstance_ReturnInstance(){
        String scheme = "https";
        String host = "api.zip-tax.com";
        String path = "request/v40";
        Pair<String, String> key = new Pair<>("key", "jkRvcDF9MVB5pxtm");
        RestTemplate restTemplate = new RestTemplate();

        FastTaxWebClientWrapper expectedFastTaxWebClientWrapper =
                new FastTaxWebClientWrapper(restTemplate,fastTaxWebClient, scheme, host, path, key);

        FastTaxWebClientWrapper actualFastTaxWebClientWrapper =
                webClientWrapperConfig.fastTaxWebClientWrapper(fastTaxWebClient);

        assertEquals(expectedFastTaxWebClientWrapper,actualFastTaxWebClientWrapper);

    }
}
