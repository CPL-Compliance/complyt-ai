package com.example.complyt.v1.validators;

import com.complyt.v1.model.AddressDto;
import com.complyt.v1.validators.DataConflictChecksProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataConflictChecksProviderTest {

    DataConflictChecksProvider<AddressDto> dataConflictChecksProvider;

    @BeforeEach
    void setup() {
        dataConflictChecksProvider = new DataConflictChecksProvider<>(null, Map.of(
                "specialVariable", (x, y) -> Mono.empty()));
    }

    @Test
    void getPathVariableCheck_NoVariableCheckFound_ReturnsDefaultCheck() {
        // Given + When
        Mono<String> stringMono = dataConflictChecksProvider.getPathVariableCheck("").flatMap(check -> check.apply(null, null));

        // Then
        StepVerifier.create(stringMono).verifyComplete();
    }

    @Test
    void getPathVariableCheck_VariableCheckFound_ReturnsSpecificCheck() {
        // Given + When
        Mono<String> stringMono = dataConflictChecksProvider.getPathVariableCheck("specialVariable").flatMap(check -> check.apply(null, null));

        // Then
        StepVerifier.create(stringMono).verifyComplete();
    }

    @Test
    void getPathVariableCheck_Null_Variable_ReturnsNullPointerException() {
        // When + Then
        assertThrows(NullPointerException.class, () -> dataConflictChecksProvider.getPathVariableCheck(null));
    }

    @Test
    void getBodyConflictCheck_NoVariableCheckFound_ReturnsDefaultCheck() {
        // Given + When
        Mono<String> stringMono = dataConflictChecksProvider.getBodyConflictCheck().flatMap(check -> check.apply(null));

        // Then
        StepVerifier.create(stringMono).verifyComplete();
    }

}