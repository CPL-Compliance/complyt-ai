package com.complyt.v1.validators;

import com.complyt.v1.models.checkables.SourceCheckable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataConflictChecksProviderTest {

    DataConflictChecksProvider<SourceCheckable> dataConflictChecksProvider;

    @BeforeEach
    void setup() {
        Map<String, BiFunction<SourceCheckable, ServerRequest, Mono<Boolean>>> variableConflictChecksMap = new HashMap();
        variableConflictChecksMap.put("source", SourceCheckable.SOURCE_CONFLICT_CHECK);
        dataConflictChecksProvider = new DataConflictChecksProvider<>(variableConflictChecksMap);
    }

    @Test
    void getCheck_ExistingVariable_ReturnsCheckMethod() {
        // given + When
        Mono<BiFunction<SourceCheckable, ServerRequest, Mono<Boolean>>> biFunctionMono = dataConflictChecksProvider.getPathVariableCheck("source");

        // Then
        StepVerifier.create(biFunctionMono).expectNext(SourceCheckable.SOURCE_CONFLICT_CHECK).verifyComplete();
    }

    @Test
    void getCheck_NotVariableCheckFound_ReturnsDefaultCheck() {
        // Given + When
        Mono<Boolean> booleanMono = dataConflictChecksProvider.getPathVariableCheck("externalId").flatMap(check -> check.apply(null, null));

        // Then
        StepVerifier.create(booleanMono).expectNext(true).verifyComplete();
    }

    @Test
    void getCheck_Null_Variable_ReturnsNullPointerException() {
        // When
        Exception nullPointerException = assertThrows(NullPointerException.class, () -> {
            dataConflictChecksProvider.getPathVariableCheck(null);
        });

        // Then
        assertEquals("pathVariable is marked non-null but is null", nullPointerException.getMessage());
    }
}