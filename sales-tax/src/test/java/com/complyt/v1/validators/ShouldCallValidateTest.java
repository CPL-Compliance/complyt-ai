package com.complyt.v1.validators;

import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShouldCallValidateTest {

    ShouldCallValidate shouldCallValidate;

    @MockBean
    ServerRequest serverRequest;

    @BeforeEach
    void setUp() {
        shouldCallValidate = new ShouldCallValidate(Map.of(
                HttpMethod.PUT, "^/v1/transactions/source/[^/]+/externalId/[^/]+$|"
                        + "^/v1/customers/source/[^/]+/externalId/[^/]+$|"
                        + "^/v1/exemptions/complytId/[^/]+$|"
                        + "^/v1/nexus/state/[^/]+$",
                HttpMethod.POST,
                "^/v1/nexus/refresh/state/[^/]+$|"
                        + "^/v1/exemptions$"));
        serverRequest = mock(ServerRequest.class);
    }

    @Test
    void apply_PostShouldReturnTrueExemption_whenMethodIsInList() {
        // Given
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/exemptions");

        // When
        boolean result = shouldCallValidate.apply(serverRequest);

        // Then
        assert result;
//        StepVerifier.create(Mono.just(result))
//                .expectNext(true)
//                .verifyComplete();
    }

    @Test
    void apply_PutShouldReturnTrueTransaction_whenMethodIsInList() {
        // Given
        when(serverRequest.method()).thenReturn(HttpMethod.PUT);
        when(serverRequest.path()).thenReturn("/v1/transactions/source/someSource/externalId/someId");

        // When
        boolean result = shouldCallValidate.apply(serverRequest);

        // Then
        StepVerifier.create(Mono.just(result))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void apply_PutShouldReturnTrueCustomer_whenMethodIsInList() {
        // Given
        when(serverRequest.method()).thenReturn(HttpMethod.PUT);
        when(serverRequest.path()).thenReturn("/v1/customers/source/1/externalId/1234");

        // When
        boolean result = shouldCallValidate.apply(serverRequest);

        // Then
        StepVerifier.create(Mono.just(result))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void apply_PutShouldReturnTrueExemption_whenMethodIsInList() {
        // Given
        when(serverRequest.method()).thenReturn(HttpMethod.PUT);
        when(serverRequest.path()).thenReturn("/v1/exemptions/complytId/c7fbc13a-7231-4e1d-8c38-47db44d4efaa");

        // When
        boolean result = shouldCallValidate.apply(serverRequest);

        // Then
        StepVerifier.create(Mono.just(result))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void apply_PutShouldReturnTrueSalesTaxTracking_whenMethodIsInList() {
        // Given
        when(serverRequest.method()).thenReturn(HttpMethod.PUT);
        when(serverRequest.path()).thenReturn("/v1/nexus/state/CA");

        // When
        boolean result = shouldCallValidate.apply(serverRequest);

        // Then
        StepVerifier.create(Mono.just(result))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void apply_PostShouldReturnTrue_whenMethodIsInList() {
        // Given
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/nexus/refresh/state/someState");

        // When
        boolean result = shouldCallValidate.apply(serverRequest);

        // Then
        StepVerifier.create(Mono.just(result))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void apply_GetShouldReturnFalse_whenMethodIsNotInList() {
        // Given
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/nexus/state/someState");

        // When
        boolean result = shouldCallValidate.apply(serverRequest);

        // Then
        StepVerifier.create(Mono.just(result))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void apply_DeleteShouldReturnFalse_whenMethodIsNotInList() {
        // Given
        when(serverRequest.method()).thenReturn(HttpMethod.DELETE);
        when(serverRequest.path()).thenReturn("/v1/exemptions/complytId/someId");

        // When
        boolean result = shouldCallValidate.apply(serverRequest);

        // Then
        StepVerifier.create(Mono.just(result))
                .expectNext(false)
                .verifyComplete();
    }
}
