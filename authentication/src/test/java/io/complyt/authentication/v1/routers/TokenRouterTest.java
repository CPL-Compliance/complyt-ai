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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@ContextConfiguration(classes = {TokenRouter.class, TokenHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class, GlobalErrorAttributes.class, GlobalExceptionHandler.class,
        CredentialsDtoQueryParamsExtractor.class})
class TokenRouterTest implements TokenRouterTestTemplate {

    @Autowired
    private TokenRouter tokenRouter;

    private Token outputToken;
    private ApiKey inputToken;

    private TokenDto inputTokenDto;
    private TokenDto outputTokenDto;

    @MockBean
    private TokenFacade tokenFacade;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        inputTokenDto = TestUtilities.createTokenDto();
        outputTokenDto = TestUtilities.createOutputTokenDto();
//        outputToken = TokenMapper.INSTANCE.tokenDtoToToken(outputTokenDto);
//        inputToken = TokenMapper.INSTANCE.tokenDtoToToken(inputTokenDto);
    }

    @Test
    @Override
    @WithMockUser
    public void post_Exists_Returns200() {
//        // When
//        when(tokenFacade.post(inputToken)).thenReturn(Mono.just(outputToken));
//
//        // Then
//        webTestClient
//                .mutateWith(csrf())
//                .post()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TokenRouter.BASE_URL)
//                        .build()).contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(inputTokenDto)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().is2xxSuccessful()
//                .equals(outputTokenDto);
    }

    @Test
    @Override
    @WithMockUser
    public void post_DoesntExist_Returns404() {
//        // When
//        when(tokenFacade.post(inputToken)).thenReturn(Mono.empty());
//
//        // Then
//        webTestClient
//                .mutateWith(csrf())
//                .post()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TokenRouter.BASE_URL)
//                        .build()).contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(inputTokenDto)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().is4xxClientError();
    }

    @Test
    @Override
    @WithMockUser
    public void post_InternalServerError_Returns500() {
//        // When
//        when(tokenFacade.post(inputToken)).thenThrow(OperationFailedException.class);
//
//        // Then
//        webTestClient
//                .mutateWith(csrf())
//                .post()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TokenRouter.BASE_URL)
//                        .build()).contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(inputTokenDto)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    public void post_NullHandler_ThrowsNullPointerException() {
//        // Given
//        TokenHandler nullTokenHandler = null;
//        TokenRouter tokenRouter = new TokenRouter();
//
//        // When
//        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
//            tokenRouter.postTokenRouterFunction(nullTokenHandler);
//        });
//
//        // Then
//        assertEquals("tokenHandler is marked non-null but is null", exception.getMessage());
    }
}