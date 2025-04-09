package com.complyt.config;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(SpringExtension.class)
public class WebClientWrapperPropertiesConfigTest {
    WebClientWrapperPropertiesConfig webClientWrapperPropertiesConfig;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

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
