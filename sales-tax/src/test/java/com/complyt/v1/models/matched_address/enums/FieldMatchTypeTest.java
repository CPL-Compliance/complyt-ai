package com.complyt.v1.models.matched_address.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldMatchTypeTest {
    @Test
    void fromScore_exactMatch() {
        // Given
        double score = 1.0;

        // When
        FieldMatchType result = FieldMatchType.fromScore(score);

        // Then
        assertEquals(FieldMatchType.EXACT, result);
        assertEquals("Exact Match", result.getDescription());
    }

    @Test
    void fromScore_goodMatch_lowerBound() {
        // Given
        double score = 0.7;

        // When
        FieldMatchType result = FieldMatchType.fromScore(score);

        // Then
        assertEquals(FieldMatchType.GOOD, result);
        assertEquals("Good Match", result.getDescription());
    }

    @Test
    void fromScore_goodMatch_upperBound() {
        // Given
        double score = 0.99;

        // When
        FieldMatchType result = FieldMatchType.fromScore(score);

        // Then
        assertEquals(FieldMatchType.GOOD, result);
    }

    @Test
    void fromScore_partialMatch_lowerBound() {
        // Given
        double score = 0.4;

        // When
        FieldMatchType result = FieldMatchType.fromScore(score);

        // Then
        assertEquals(FieldMatchType.PARTIAL, result);
        assertEquals("Partial Match", result.getDescription());
    }

    @Test
    void fromScore_partialMatch_upperBound() {
        // Given
        double score = 0.69;

        // When
        FieldMatchType result = FieldMatchType.fromScore(score);

        // Then
        assertEquals(FieldMatchType.PARTIAL, result);
    }

    @Test
    void fromScore_noMatch_belowPartialThreshold() {
        // Given
        double score = 0.39;

        // When
        FieldMatchType result = FieldMatchType.fromScore(score);

        // Then
        assertEquals(FieldMatchType.NO_MATCH, result);
        assertEquals("Did Not Match", result.getDescription());
    }

    @Test
    void fromScore_noMatch_zero() {
        // Given
        double score = 0.0;

        // When
        FieldMatchType result = FieldMatchType.fromScore(score);

        // Then
        assertEquals(FieldMatchType.NO_MATCH, result);
    }

    @Test
    void fromScore_negativeScore_treatedAsNoMatch() {
        // Given
        double score = -0.5;

        // When
        FieldMatchType result = FieldMatchType.fromScore(score);

        // Then
        assertEquals(FieldMatchType.NO_MATCH, result);
    }
}