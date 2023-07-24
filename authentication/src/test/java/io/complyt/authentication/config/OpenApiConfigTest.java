package io.complyt.authentication.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                        .title("Authentication API")
                        .description("Authentication API")
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