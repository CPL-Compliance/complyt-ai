package io.complyt.authentication.v1.routers;

import io.complyt.authentication.config.ApiExceptionConfig;
import io.complyt.authentication.security.AesSecretKeyUtils;
import io.complyt.authentication.security.ApiKeyGenerator;
import io.complyt.authentication.services.AesSecretKeyService;
import io.complyt.authentication.v1.exceptions.GlobalErrorAttributes;
import io.complyt.authentication.v1.exceptions.GlobalExceptionHandler;
import io.complyt.authentication.v1.handlers.SecretKeyHandler;
import io.complyt.authentication.v1.handlers.TokenHandler;
import io.complyt.authentication.v1.models.SecretKeyDto;
import io.complyt.authentication.v1.models.TokenDto;
import io.complyt.authentication.v1.validators.ValidatorConfig;
import io.complyt.authentication.v1.validators.query_params.ApiKeyQueryParamsExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import testUtils.TestUtilities;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@ContextConfiguration(classes = {SecretKeyRouter.class, SecretKeyHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class, GlobalErrorAttributes.class, GlobalExceptionHandler.class,
        ApiKeyQueryParamsExtractor.class, AesSecretKeyUtils.class})
class SecretKeyRouterTest implements SecretKeyRouterTestTemplate {

    @Autowired
    private SecretKeyRouter secretKeyRouter;


    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    AesSecretKeyService aesSecretKeyService;

    @Test
    void getSecretKeyRouterFunction() {
    }

    @Test
    @WithMockUser
    @Override
    public void get_Exists_Returns200WithList() {
        // Given
        SecretKey expectedSecretKey = AesSecretKeyUtils.generateAesKey(256);
        String expectedSecretKeyStr = AesSecretKeyUtils.convertSecretKeyToString(expectedSecretKey);
        SecretKeyDto secretKeyDto = new SecretKeyDto(expectedSecretKeyStr);

        // When
        when(aesSecretKeyService.generate256AesKey()).thenReturn(expectedSecretKey);
        when(aesSecretKeyService.convertSecretKeyToString(expectedSecretKey)).thenReturn(expectedSecretKeyStr);

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SecretKeyRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SecretKeyDto.class)
                .isEqualTo(secretKeyDto);
    }

    @Override
    public void get_EmptyCollection_Returns200WithEmptyList() {

    }

    @Override
    public void get_UnauthenticatedUser_Returns401() {

    }

    @Override
    public void get_UserWithoutAuthorities_Returns403() {

    }

    @Override
    public void get_InternalServerError_Returns500() {

    }

    @Override
    public void get_NullHandler_ThrowsNullPointerException() {

    }
}