package io.complyt.authentication.v1.routers;

import io.complyt.authentication.config.ApiExceptionConfig;
import io.complyt.authentication.security.AesSecretKeyUtils;
import io.complyt.authentication.services.AesSecretKeyService;
import io.complyt.authentication.v1.exceptions.GlobalErrorAttributes;
import io.complyt.authentication.v1.exceptions.GlobalExceptionHandler;
import io.complyt.authentication.v1.handlers.SecretKeyHandler;
import io.complyt.authentication.v1.models.SecretKeyDto;
import io.complyt.authentication.v1.validators.ValidatorConfig;
import io.complyt.authentication.v1.validators.query_params.QueryParamsExtractorEmpty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import test_utils.unit_tests.templates.GetRouterTestMonoTemplate;
import test_utils.unit_tests.templates.GetRouterTestSecurityTemplate;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@ContextConfiguration(classes = {SecretKeyRouter.class, SecretKeyHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class, GlobalErrorAttributes.class, GlobalExceptionHandler.class,
        QueryParamsExtractorEmpty.class})
class SecretKeyRouterTest implements GetRouterTestMonoTemplate, GetRouterTestSecurityTemplate {

    @Autowired
    SecretKeyRouter secretKeyRouter;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    AesSecretKeyService aesSecretKeyService;

    @Test
    @WithMockUser
    @Override
    public void get_Exists_Returns200() {
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

    @Test
    @Override
    public void get_UnauthenticatedUser_Returns401() {
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SecretKeyRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    @WithMockUser
    @Override
    public void get_InternalServerError_Returns500() {
        // When
        when(aesSecretKeyService.generate256AesKey()).thenThrow(new RuntimeException());

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SecretKeyRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    public void get_NullHandler_ThrowsNullPointerException() {
        // Given
        SecretKeyRouter secretKeyRouter = new SecretKeyRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            secretKeyRouter.getSecretKeyRouterFunction(null);
        });

        // Then
        assertEquals("secretKeyHandler is marked non-null but is null", exception.getMessage());
    }
}