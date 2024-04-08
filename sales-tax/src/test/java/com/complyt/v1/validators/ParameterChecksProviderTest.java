package com.complyt.v1.validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ParameterChecksProviderTest {

    ParameterChecksProvider parameterChecksProvider;

    @MockBean
    ServerRequest serverRequest;

    @BeforeEach
    void setUp() {
        Map<String, Function<String, Mono<String>>> paramChecksMap = new HashMap<>();
        paramChecksMap.put("param1", param -> Mono.just("Param1: " + param));
        paramChecksMap.put("param2", param -> Mono.just("Param2: " + param));
        parameterChecksProvider = new ParameterChecksProvider(paramChecksMap);

        serverRequest = mock(ServerRequest.class);
    }

    @Test
    void getFunctionCheck_existingParam_shouldReturnFunction() {
        String paramName = "param1";
        Mono<Function<String, Mono<String>>> functionCheck = parameterChecksProvider.getFunctionCheck(paramName);
        assertNotNull(functionCheck);
        functionCheck.subscribe(Assertions::assertNotNull);
    }

    @Test
    void getFunctionCheck_nonExistingParam_shouldReturnEmptyFunction() {
        String paramName = "nonExistingParam";
        Mono<Function<String, Mono<String>>> functionCheck = parameterChecksProvider.getFunctionCheck(paramName);
        assertNotNull(functionCheck);
        functionCheck.subscribe(check -> {
            assertNotNull(check);
            assertTrue(check.apply("testParam").blockOptional().isEmpty());
        });
    }

    @Test
    void getCheck_NotVariableCheckFound_ReturnsDefaultCheck() {
        // Given + When
        Mono<String> booleanMono = parameterChecksProvider.getFunctionCheck("externalId").flatMap(check -> check.apply(null));

        // Then
        StepVerifier.create(booleanMono).expectNextCount(0).verifyComplete();
    }

    @Test
    void doesParamExist_existingParam_shouldReturnTrue() {
        when(serverRequest.queryParam("param1")).thenReturn("value".describeConstable());

        Mono<Boolean> doesExist = parameterChecksProvider.doesParamExist(serverRequest);
        assertTrue(doesExist.blockOptional().orElse(false));
    }

    @Test
    void doesParamExist_nonExistingParam_shouldReturnFalse() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.queryParam("nonExistingParam")).thenReturn(null);

        Mono<Boolean> doesExist = parameterChecksProvider.doesParamExist(serverRequest);
        assertFalse(doesExist.blockOptional().orElse(true));
    }

    @Test
    void getFunctionCheck_emptyParamName_shouldReturnEmptyFunction() {
        Mono<Function<String, Mono<String>>> functionCheck = parameterChecksProvider.getFunctionCheck("");
        assertNotNull(functionCheck);
        functionCheck.subscribe(check -> {
            assertNotNull(check);
            assertTrue(check.apply("testParam").blockOptional().isEmpty());
        });
    }

    @Test
    void getFunctionCheck_caseInsensitiveParamName_shouldReturnFunction() {
        String paramName = "PARAM1";
        Mono<Function<String, Mono<String>>> functionCheck = parameterChecksProvider.getFunctionCheck(paramName);
        assertNotNull(functionCheck);
        functionCheck.subscribe(Assertions::assertNotNull);
    }

    @Test
    void getCheck_Null_Variable_ReturnsNullPointerException() {
        // When
        Exception nullPointerException = assertThrows(NullPointerException.class, () -> parameterChecksProvider.getFunctionCheck(null));

        // Then
        assertEquals("uriVariable is marked non-null but is null", nullPointerException.getMessage());
    }

}