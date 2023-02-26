package com.complyt.v1.validators;

import com.complyt.v1.models.properties.SourceCheckable;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

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
        Mono<BiFunction<?, ServerRequest, Mono<Boolean>>> biFunctionMono = dataConflictChecksProvider.getCheck("source");

        // Then
        StepVerifier.create(biFunctionMono).expectNext(SourceCheckable.SOURCE_CONFLICT_CHECK).verifyComplete();
    }

    @Test
    void getCheck_NullVariable_ReturnsNullPointerException() {
        // given + When
        Mono<BiFunction<?, ServerRequest, Mono<Boolean>>> biFunctionMono = dataConflictChecksProvider.getCheck("source");

        // Then
        StepVerifier.create(biFunctionMono).expectNext(SourceCheckable.SOURCE_CONFLICT_CHECK).verifyComplete();
    }
}