package io.complyt.authentication.v1.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ParameterChecksProviderTest {

    private ParameterChecksProvider parameterChecksProvider;

    @BeforeEach
    void setUp() {
        // Mock function checks
        Function<String, Mono<String>> mockCheck = param -> param.equals("valid") ? Mono.just("valid") : Mono.error(new IllegalArgumentException("Invalid"));

        // Initialize ParameterChecksProvider with a test map
        parameterChecksProvider = new ParameterChecksProvider(Map.of(
                "param1", mockCheck,
                "param2", param -> Mono.empty() // Always empty check
        ));
    }

    @Test
    void getFunctionCheck_ValidParam_ReturnsFunction() {
        // When
        Mono<Function<String, Mono<String>>> functionMono = parameterChecksProvider.getFunctionCheck("param1");

        // Then
        StepVerifier.create(functionMono)
                .assertNext(function -> {
                    // Verify function behavior
                    StepVerifier.create(function.apply("valid"))
                            .expectNext("valid")
                            .verifyComplete();
                })
                .verifyComplete();
    }

    @Test
    void getFunctionCheck_InvalidParam_ReturnsEmptyFunction() {
        // When
        Mono<Function<String, Mono<String>>> functionMono = parameterChecksProvider.getFunctionCheck("unknownParam");

        // Then
        StepVerifier.create(functionMono)
                .assertNext(function -> {
                    // Verify function always returns empty Mono
                    StepVerifier.create(function.apply("anyValue"))
                            .verifyComplete();
                })
                .verifyComplete();
    }

    @Test
    void getAccessToken_forPartner_partnerTenantIdIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            parameterChecksProvider.getFunctionCheck(null);
        });

        assertEquals(nullPointerException.getMessage(), "uriVariable is marked non-null but is null");
    }

    @Test
    void doesParamExist_QueryParamExists_ReturnsTrue() {
        // Given
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.queryParam("param1")).thenReturn(java.util.Optional.of("someValue"));

        // When
        Mono<Boolean> result = parameterChecksProvider.doesParamExist(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void doesParamExist_NoMatchingQueryParam_ReturnsFalse() {
        // Given
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.queryParam(anyString())).thenReturn(java.util.Optional.empty());

        // When
        Mono<Boolean> result = parameterChecksProvider.doesParamExist(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }
}
