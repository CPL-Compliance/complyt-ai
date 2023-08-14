package io.complyt.authentication.v1.routers;

import io.complyt.authentication.config.ApiExceptionConfig;
import io.complyt.authentication.config.SecurityConfig;
import io.complyt.authentication.facades.ApiKeyFacade;
import io.complyt.authentication.v1.exceptions.GlobalErrorAttributes;
import io.complyt.authentication.v1.exceptions.GlobalExceptionHandler;
import io.complyt.authentication.v1.handlers.ApiKeyHandler;
import io.complyt.authentication.v1.validators.ValidatorConfig;
import io.complyt.authentication.v1.validators.query_params.ApiKeyQueryParamsExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest
@ContextConfiguration(classes = {ApiKeyRouter.class, ApiKeyHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalExceptionHandler.class,
        GlobalErrorAttributes.class,
        SecurityConfig.class,
        ApiKeyQueryParamsExtractor.class
})
class ApiKeyRouterTest {
    @Autowired
    ApiKeyRouter apiKeyRouter;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ApiKeyFacade apiKeyFacade;

    @Test
    void postCredentialsRouterFunction() {
    }
}