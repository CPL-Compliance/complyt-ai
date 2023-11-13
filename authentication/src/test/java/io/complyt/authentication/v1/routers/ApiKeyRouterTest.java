package io.complyt.authentication.v1.routers;

import io.complyt.authentication.config.ApiExceptionConfig;
import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.facades.ApiKeyFacade;
import io.complyt.authentication.repositories.exceptions.OperationFailedException;
import io.complyt.authentication.v1.exceptions.GlobalErrorAttributes;
import io.complyt.authentication.v1.exceptions.GlobalExceptionHandler;
import io.complyt.authentication.v1.handlers.ApiKeyHandler;
import io.complyt.authentication.v1.mappers.ApiKeyMapper;
import io.complyt.authentication.v1.mappers.CredentialsMapper;
import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.models.CredentialsDto;
import io.complyt.authentication.v1.validators.ValidatorConfig;
import io.complyt.authentication.v1.validators.query_params.QueryParamsExtractorEmpty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import test_utils.unit_tests.TestUtilities;
import test_utils.unit_tests.templates.PostCreatedRouterMonoTest;
import test_utils.unit_tests.templates.PostRouterTestSecurityTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@ContextConfiguration(classes = {ApiKeyRouter.class, ApiKeyHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class, GlobalExceptionHandler.class, GlobalErrorAttributes.class,
        QueryParamsExtractorEmpty.class})
class ApiKeyRouterTest implements PostCreatedRouterMonoTest, PostRouterTestSecurityTemplate {
    @Autowired
    ApiKeyRouter apiKeyRouter;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ApiKeyFacade apiKeyFacade;

    CredentialsDto credentialsDto;

    Credentials credentials;

    @BeforeEach
    void postCredentialsRouterFunction() {
        credentialsDto = TestUtilities.createCredentialsDto();
        credentials = CredentialsMapper.INSTANCE.credentialsDtoTocredentials(credentialsDto);
    }

    @Test
    @WithMockUser
    @Override
    public void post_Exists_Returns201() {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        ApiKeyDto apiKeyDto = ApiKeyMapper.INSTANCE.apiKeyToApiKeyDto(apiKey);

        // When
        when(apiKeyFacade.saveCredentials(credentials)).thenReturn(Mono.just(apiKey));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiKeyRouter.BASE_URL)
                        .build())
                .bodyValue(credentialsDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ApiKeyDto.class)
                .isEqualTo(apiKeyDto);
    }

    @Test
    @Override
    @WithMockUser
    public void post_DoesntExist_Returns404() {
        // When
        when(apiKeyFacade.saveCredentials(credentials)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiKeyRouter.BASE_URL)
                        .build())
                .bodyValue(credentialsDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void post_InternalServerError_Returns500() {
        // When
        when(apiKeyFacade.saveCredentials(credentials)).thenThrow(OperationFailedException.class);

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiKeyRouter.BASE_URL)
                        .build())
                .bodyValue(credentialsDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    public void post_NullHandler_ThrowsNullPointerException() {
        // Given
        ApiKeyRouter apiKeyRouter = new ApiKeyRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            apiKeyRouter.postCredentialsRouterFunction(null);
        });

        // Then
        assertEquals("apiKeyHandler is marked non-null but is null", exception.getMessage());
    }

    @Test
    @Override
    public void post_UnauthenticatedUser_Returns401() {
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiKeyRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();

    }

    @Test
    @WithMockUser
    @Override
    public void post_missingCsrfToken_return403() {
        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiKeyRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }
}