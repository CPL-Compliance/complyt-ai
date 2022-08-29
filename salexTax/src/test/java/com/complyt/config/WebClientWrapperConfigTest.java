package com.complyt.config;

import com.complyt.business.sales_tax.sales_tax_web_clients.FastTaxWebClientWrapper;
import com.complyt.business.sales_tax.sales_tax_web_clients.ZipTaxWebClientWrapper;
import com.complyt.config.web_clients.FastTaxWebClientWrapperProperties;
import com.complyt.config.web_clients.WebClientWrapperConfig;
import com.complyt.config.web_clients.ZipTaxWebClientWrapperProperties;
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
    void zipTaxWebClientWrapper_CreateInstance_ReturnInstance() {
        ZipTaxWebClientWrapper expectedZipTaxWebClientWrapper = new ZipTaxWebClientWrapper(webClient,
                ZipTaxWebClientWrapperProperties.SCHEME,
                ZipTaxWebClientWrapperProperties.HOST,
                ZipTaxWebClientWrapperProperties.PATH,
                ZipTaxWebClientWrapperProperties.KEY);

        ZipTaxWebClientWrapper actualZipTaxWebClientWrapper =
                webClientWrapperConfig.zipTaxWebClientWrapper(webClient);

        assertEquals(expectedZipTaxWebClientWrapper, actualZipTaxWebClientWrapper);
    }

    @Test
    void fastTaxWebClientWrapper_SetInstance_ReturnInstance() {
        FastTaxWebClientWrapper expectedFastTaxWebClientWrapper = new FastTaxWebClientWrapper(webClient,
                FastTaxWebClientWrapperProperties.SCHEME,
                FastTaxWebClientWrapperProperties.HOST,
                FastTaxWebClientWrapperProperties.PATH,
                FastTaxWebClientWrapperProperties.KEY);

        FastTaxWebClientWrapper actualFastTaxWebClientWrapper =
                webClientWrapperConfig.fastTaxWebClientWrapper(webClient);

        assertEquals(expectedFastTaxWebClientWrapper, actualFastTaxWebClientWrapper);
    }
}
