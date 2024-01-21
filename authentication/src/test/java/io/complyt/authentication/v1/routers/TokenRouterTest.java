package io.complyt.authentication.v1.routers;

import io.complyt.authentication.config.ApiExceptionConfig;
import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.facades.TokenFacade;
import io.complyt.authentication.repositories.exceptions.OperationFailedException;
import io.complyt.authentication.v1.config.error_messages.GenericErrorMessages;
import io.complyt.authentication.v1.exceptions.GlobalErrorAttributes;
import io.complyt.authentication.v1.exceptions.GlobalExceptionHandler;
import io.complyt.authentication.v1.handlers.TokenHandler;
import io.complyt.authentication.v1.mappers.TokenMapper;
import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.models.TokenDto;
import io.complyt.authentication.v1.validators.ValidatorConfig;
import io.complyt.authentication.v1.validators.query_params.QueryParamsExtractorEmpty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import test_utils.unit_tests.TestUtilities;
import test_utils.unit_tests.templates.PostOkRouterMonoTest;
import test_utils.unit_tests.templates.PostRouterTestSecurityTemplate;

import java.util.LinkedHashMap;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@ContextConfiguration(classes = {TokenRouter.class, TokenHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class, GlobalErrorAttributes.class, GlobalExceptionHandler.class,
        QueryParamsExtractorEmpty.class})
class TokenRouterTest implements PostOkRouterMonoTest, PostRouterTestSecurityTemplate {

    @Autowired
    TokenRouter tokenRouter;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TokenFacade tokenFacade;

    private Token token;

    private TokenDto tokenDto;

    @BeforeEach
    void setUp() {
        token = TestUtilities.createToken();
        tokenDto = TokenMapper.INSTANCE.tokentoTokenDto(token);
    }

    @Test
    @Override
    @WithMockUser
    public void post_Exists_Returns200() {
        // Given
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();
        ApiKey apiKey = TestUtilities.createApiKey();

        // When
        when(tokenFacade.getToken(apiKey)).thenReturn(Mono.just(token));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build())
                .bodyValue(apiKeyDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenDto.class)
                .value(actualTokenDto -> actualTokenDto, equalTo(tokenDto));
    }

    @Test
    @Override
    @WithMockUser
    public void post_DoesntExist_Returns404() {
        // Given
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();
        ApiKey apiKey = TestUtilities.createApiKey();

        // When
        when(tokenFacade.getToken(apiKey)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build())
                .bodyValue(apiKeyDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser
    public void post_ResourceDoesntExist_Returns404() {
        // Given
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();
        ApiKey apiKey = TestUtilities.createApiKey();

        // When
        when(tokenFacade.getToken(apiKey)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL + "_test")
                        .build())
                .bodyValue(apiKeyDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void post_InternalServerError_Returns500() {
        // Given
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();
        ApiKey apiKey = TestUtilities.createApiKey();

        // When
        when(tokenFacade.getToken(apiKey)).thenThrow(OperationFailedException.class);

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build())
                .bodyValue(apiKeyDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    public void post_NullHandler_ThrowsNullPointerException() {
        // Given
        TokenRouter tokenRouter = new TokenRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            tokenRouter.postTokenRouterFunction(null);
        });

        // Then
        assertEquals("tokenHandler is marked non-null but is null", exception.getMessage());
    }

    @Override
    @Test
    @WithMockUser
    public void post_UnsupportedMediaType_Returns415() {

        // Given + When + Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("{}")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.UNSUPPORTED_MEDIA_TYPE, map.get("message")));
    }

    @Test
    @Override
    public void post_UnauthenticatedUser_Returns401() {
        // Given
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build())
                .bodyValue(apiKeyDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    @WithMockUser
    @Override
    public void post_missingCsrfToken_return403() {
        // Given
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();

        // Then
        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build())
                .bodyValue(apiKeyDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @WithMockUser
    public void post_invalidApiKeyFormat_return400() {
        ApiKeyDto apiKeyDto = new ApiKeyDto(TestUtilities.invalidApiKeyClientIdStr, TestUtilities.invalidApiKeyClientSecretStr);

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build())
                .bodyValue(apiKeyDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatusCode.valueOf(400));
    }

    @Test
    @WithMockUser
    public void post_apiKeyClientIdValueIsMissing_Returns400() {
        ApiKeyDto apiKeyDto = new ApiKeyDto("", "3572db2e-486b-480a-995b-2e4d2b9104fa");

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build())
                .bodyValue(apiKeyDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser
    public void post_apiKeyClientSecretValueIsMissing_Returns400() {
        ApiKeyDto apiKeyDto = new ApiKeyDto("3572db2e-486b-480a-995b-2e4d2b9104fa", "");

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build())
                .bodyValue(apiKeyDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }
}