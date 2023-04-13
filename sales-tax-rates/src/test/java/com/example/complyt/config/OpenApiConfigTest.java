package com.example.complyt.config;

import com.complyt.config.OpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class OpenApiConfigTest {

    private static OpenApiConfig openApiConfig;

    @BeforeAll
    static void beforeAll() {
        openApiConfig = new OpenApiConfig();
    }

    @Test
    void springShopOpenAPI() {
        // Given
        OpenAPI expectedOpenApi = new OpenAPI()
                .info(new Info()
                        .title("Sales-Tax-Rates API")
                        .description("Sales-Tax-Rates API")
                        .version("v0.0.1")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));

        // When
        OpenAPI actualOpenAPI = openApiConfig.openAPIConfiguration();

        // Then
        assertEquals(expectedOpenApi, actualOpenAPI);
    }
}