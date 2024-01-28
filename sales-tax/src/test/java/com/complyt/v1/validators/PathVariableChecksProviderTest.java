package com.complyt.v1.validators;

import com.complyt.v1.validators.param_checker.ParamCheckerFunctions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathVariableChecksProviderTest {

    @MockBean
    ServerRequest serverRequest;

    ParameterChecksProvider parameterChecksProvider;

    @BeforeEach
    void setup() {
        parameterChecksProvider = new ParameterChecksProvider(Map.of(
                "param", ParamCheckerFunctions.PAGE_CHECK));
        serverRequest = mock(ServerRequest.class);
    }

    @Test
    void getFunctionCheck_ExistingParam_ReturnsFunction() {
        // Given + When
        String paramName = "param";
        Mono<Function<String, Mono<String>>> result = parameterChecksProvider.getFunctionCheck(paramName);

        // Then
        StepVerifier.create(result).expectNext(ParamCheckerFunctions.PAGE_CHECK).verifyComplete();
    }

    @Test
    void getFunctionCheck_NonExistingParam_ReturnsEmptyFunction() {
        // Given + When
        String paramName = "nonExistingParam";

        Mono<Function<String, Mono<String>>> result = parameterChecksProvider.getFunctionCheck(paramName);
        Mono<String> functionResult = Objects.requireNonNull(result.block()).apply("testValue");

        // Then
        StepVerifier.create(functionResult).verifyComplete();

    }

    @Test
    void doesParamExist_ParamExists_ReturnsTrue() {
        // Given + When
        when(serverRequest.queryParam("param")).thenReturn("value".describeConstable());
        Mono<Boolean> result = parameterChecksProvider.doesParamExist(serverRequest);
        // Then
        StepVerifier.create(result).expectNext(true).verifyComplete();

    }

    @Test
    void doesParamExist_ParamDoesNotExist_ReturnsFalse() {
        // Given + When
        when(serverRequest.queryParam("nonExistingParam")).thenReturn(Optional.empty());

        Mono<Boolean> result = parameterChecksProvider.doesParamExist(serverRequest);
        // Then
        StepVerifier.create(result).expectNext(false).verifyComplete();

    }


}
