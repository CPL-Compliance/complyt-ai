package com.complyt.v1.validators;

import com.complyt.v1.models.checkables.SourceCheckable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.BiFunction;

class DataConflictChecksProviderTest {

    ValidatorConfig validatorConfig;

    DataConflictChecksProvider dataConflictChecksProvider;

    @BeforeEach
    void setup() {
        validatorConfig = new ValidatorConfig();
        dataConflictChecksProvider = validatorConfig.dataConflictChecksProvider();
    }

    @Test
    void getCheck_ExistingVariable_ReturnsCheckMethod() {
        // given + When
        Mono<BiFunction<?, ServerRequest, Mono<Boolean>>> biFunctionMono = dataConflictChecksProvider.getPathVariableCheck("source");

        // Then
        StepVerifier.create(biFunctionMono).expectNext(SourceCheckable.SOURCE_CONFLICT_CHECK).verifyComplete();
    }

    @Test
    void getCheck_NullVariable_ReturnsNullPointerException() {
        // given + When
        Mono<BiFunction<?, ServerRequest, Mono<Boolean>>> biFunctionMono = dataConflictChecksProvider.getPathVariableCheck("source");

        // Then
        StepVerifier.create(biFunctionMono).expectNext(SourceCheckable.SOURCE_CONFLICT_CHECK).verifyComplete();
    }
}