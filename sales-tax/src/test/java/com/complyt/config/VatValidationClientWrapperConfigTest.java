package com.complyt.config;

import com.complyt.business.vat_validation.web_clients.VatValidationWebClientWrapper;
import com.complyt.business.vat_validation.web_clients.VowVatValidationWebClientWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VatValidationClientWrapperConfigTest {
    @InjectMocks
    VatValidationClientWrapperConfig vatValidationClientWrapperConfig;

    @Mock
    WebClient vowVatValidationWebClient;

    @Mock
    WebClientWrapperProperties vowVatValidationWebClientWrapperProperties;

    @Test
    void vowVatValidationWebClientWrapper() {
        // Given
        VowVatValidationWebClientWrapper expectedClientWrapper = new VowVatValidationWebClientWrapper(vowVatValidationWebClient,
                "schema", "host", "path");

        WebClientWrapperProperties vowVatValidationWebClientWrapperProperties = new WebClientWrapperProperties(
                "schema", "host", "path");

        // When + Then
        VatValidationWebClientWrapper resultVowVatValidationWebClientWrapper = vatValidationClientWrapperConfig
                .vowVatValidationWebClientWrapper(vowVatValidationWebClient, vowVatValidationWebClientWrapperProperties);

        assertEquals(expectedClientWrapper.toString(), resultVowVatValidationWebClientWrapper.toString());
    }
}
