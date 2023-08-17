package io.complyt.authentication.v1.routers;

import io.complyt.authentication.config.ApiExceptionConfig;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.facades.TokenFacade;
import io.complyt.authentication.repositories.exceptions.OperationFailedException;
import io.complyt.authentication.v1.exceptions.GlobalErrorAttributes;
import io.complyt.authentication.v1.exceptions.GlobalExceptionHandler;
import io.complyt.authentication.v1.handlers.TokenHandler;
import io.complyt.authentication.v1.mappers.TokenMapper;
import io.complyt.authentication.v1.models.ApiKey;
import io.complyt.authentication.v1.models.TokenDto;
import io.complyt.authentication.v1.validators.ValidatorConfig;
import io.complyt.authentication.v1.validators.query_params.ApiKeyQueryParamsExtractor;
import io.complyt.authentication.v1.validators.query_params.CredentialsDtoQueryParamsExtractor;
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
import testUtils.TestUtilities;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@ContextConfiguration(classes = {TokenRouter.class, TokenHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class, GlobalErrorAttributes.class, GlobalExceptionHandler.class,
        ApiKeyQueryParamsExtractor.class})
class TokenRouterTest implements TokenRouterTestTemplate {

    @Autowired
    private TokenRouter tokenRouter;

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
        ApiKey apiKey = TestUtilities.createApiKey();

        // When
        when(tokenFacade.getToken(apiKey)).thenReturn(Mono.just(token));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .queryParam("api_key", TestUtilities.apiKeyStr)
                        .build())
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
        ApiKey apiKey = TestUtilities.createApiKey();

        // When
        when(tokenFacade.getToken(apiKey)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .queryParam("api_key", TestUtilities.apiKeyStr)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void post_InternalServerError_Returns500() {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();

        // When
        when(tokenFacade.getToken(apiKey)).thenThrow(OperationFailedException.class);

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .queryParam("api_key", TestUtilities.apiKeyStr)
                        .build())
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
}