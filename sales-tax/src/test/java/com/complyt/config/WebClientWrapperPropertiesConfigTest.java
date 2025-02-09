package com.complyt.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class WebClientWrapperPropertiesConfigTest {
    WebClientWrapperPropertiesConfig webClientWrapperPropertiesConfig;

    @BeforeEach
    void setUp() {
        webClientWrapperPropertiesConfig = new WebClientWrapperPropertiesConfig();
    }

    @Test
    void vowVatValidationWebClientWrapperProperties_BuildVowWebClient() {
        // Given
        WebClientWrapperProperties expectedVowWebClientProperties = WebClientWrapperProperties.builder()
                .scheme("https")
                .host("ec.europa.eu")
                .path("taxation_customs/vies/rest-api/check-vat-number")
                .build();

        // When
        WebClientWrapperProperties actualVowWebClientProperties = webClientWrapperPropertiesConfig.vowVatValidationWebClientWrapperProperties();

        // Then
        assertEquals(expectedVowWebClientProperties, actualVowWebClientProperties);
    }
}
