package com.complyt.v1.validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

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
        // Set up the ParameterChecksProvider instance with some test data
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

}