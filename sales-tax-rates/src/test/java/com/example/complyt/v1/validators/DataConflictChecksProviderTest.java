package com.example.complyt.v1.validators;

import com.complyt.v1.model.AddressDto;
import com.complyt.v1.validators.DataConflictChecksProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.webjars.NotFoundException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataConflictChecksProviderTest {

    DataConflictChecksProvider<AddressDto> dataConflictChecksProvider;

    @BeforeEach
    void setup() {
        dataConflictChecksProvider = new DataConflictChecksProvider<>(null, Map.of(
                "specialVariable", (x, y) -> Mono.just(true)));
    }

    @Test
    void getPathVariableCheck_NoVariableCheckFound_ReturnsDefaultCheck() {
        // Given + When
        Mono<Boolean> booleanMono = dataConflictChecksProvider.getPathVariableCheck("").flatMap(check -> check.apply(null, null));

        // Then
        StepVerifier.create(booleanMono).expectNext(true).verifyComplete();
    }

    @Test
    void getPathVariableCheck_VariableCheckFound_ReturnsSpecificCheck() {
        // Given + When
        Mono<Boolean> booleanMono = dataConflictChecksProvider.getPathVariableCheck("specialVariable").flatMap(check -> check.apply(null, null));

        // Then
        StepVerifier.create(booleanMono).expectNext(true).verifyComplete();
    }

    @Test
    void getPathVariableCheck_Null_Variable_ReturnsNullPointerException() {
        // When + Then
        assertThrows(NullPointerException.class, () -> dataConflictChecksProvider.getPathVariableCheck(null));
    }

    @Test
    void getBodyConflictCheck_NoVariableCheckFound_ReturnsDefaultCheck() {
        // Given + When
        Mono<Boolean> booleanMono = dataConflictChecksProvider.getBodyConflictCheck().flatMap(check -> check.apply(null));

        // Then
        StepVerifier.create(booleanMono).expectNext(true).verifyComplete();
    }

}