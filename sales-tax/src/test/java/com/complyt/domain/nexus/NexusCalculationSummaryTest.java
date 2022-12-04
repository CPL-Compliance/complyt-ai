package com.complyt.domain.nexus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NexusCalculationSummaryTest {

    private NexusCalculationSummary nexusCalculationSummary;

    @BeforeEach
    void setup() {
        nexusCalculationSummary = createNexusCalculationSummary();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "NexusCalculationSummary(count=0, amount=0.0)";

        // When
        String actualString = nexusCalculationSummary.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameNexusCalculationSummary_ReturnTrue() {
        // Given
        NexusCalculationSummary givenNexusCalculationSummary = createNexusCalculationSummary();

        // When
        boolean actualBoolean = nexusCalculationSummary.equals(givenNexusCalculationSummary);

        // Then
        assertTrue(actualBoolean);
    }

    private NexusCalculationSummary createNexusCalculationSummary() {
        return new NexusCalculationSummary(0l, 0f);
    }

}