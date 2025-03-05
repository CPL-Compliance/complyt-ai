package com.complyt.v1.validators.param_checker;

import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface ParameterCheckableDefault {
    static Function<String, Mono<String>> createParamCheckerFunction(
            String regexExpression,
            String errorMessage
    ) {
        return (value) -> value.matches(regexExpression)
                ? Mono.empty()
                : Mono.just(errorMessage);
    }
}