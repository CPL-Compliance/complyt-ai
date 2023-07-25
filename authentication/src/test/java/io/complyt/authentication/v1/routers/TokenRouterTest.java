package io.complyt.authentication.v1.routers;

import io.complyt.authentication.config.ApiExceptionConfig;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.facades.TokenFacade;
import io.complyt.authentication.v1.exceptions.GlobalErrorAttributes;
import io.complyt.authentication.v1.exceptions.GlobalExceptionHandler;
import io.complyt.authentication.v1.handlers.TokenHandler;
import io.complyt.authentication.v1.models.TokenDto;
import io.complyt.authentication.v1.validators.ValidatorConfig;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@ContextConfiguration(classes = {TokenRouter.class, TokenHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class, GlobalErrorAttributes.class, GlobalExceptionHandler.class})
class TokenRouterTest implements TokenRouterTestTemplate {

    @Autowired
    private TokenRouter tokenRouter;

    private Token token;

    private TokenDto tokenDto;

    @MockBean
    private TokenFacade tokenFacade;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        tokenDto = TestUtilities.createTokenDto();
        token = TestUtilities.createToken();
    }

    @Test
    @Override
    @WithMockUser
    public void post_Exists_Returns200() {
        // Given


        // When
        when(tokenFacade.get(token)).thenReturn(Mono.just(token));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tokenDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .equals(tokenDto);
    }

    @Test
    @Override
    public void post_DoesntExist_Returns404() {

    }

    @Test
    @Override
    public void post_InternalServerError_Returns500() {

    }

    @Test
    @Override
    public void post_NullHandler_ThrowsNullPointerException() {

    }
}