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
import testUtils.unitTests.templates.endpoints.PostRouterMonoTest;
import testUtils.unitTests.templates.endpoints.PostRouterTestSecurityTemplate;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest
@ContextConfiguration(classes = {ApiKeyRouter.class, ApiKeyHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalExceptionHandler.class,
        GlobalErrorAttributes.class,
        SecurityConfig.class,
        ApiKeyQueryParamsExtractor.class
})
class ApiKeyRouterTest implements PostRouterMonoTest, PostRouterTestSecurityTemplate {
    @Autowired
    ApiKeyRouter apiKeyRouter;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ApiKeyFacade apiKeyFacade;

    @Test
    void postCredentialsRouterFunction() {
    }

    @Override
    public void post_Exists_Returns200() {

    }

    @Override
    public void post_DoesntExist_Returns404() {

    }

    @Override
    public void post_InternalServerError_Returns500() {

    }

    @Override
    public void post_NullHandler_ThrowsNullPointerException() {

    }

    @Override
    public void post_UnauthenticatedUser_Returns401() {

    }

    @Override
    public void post_missingCsrfToken_return403() {

    }
}