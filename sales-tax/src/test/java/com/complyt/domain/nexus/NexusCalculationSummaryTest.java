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

    private NexusCalculationSummary createNexusCalculationSummary() {
        return new NexusCalculationSummary(0L, 0f);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "NexusCalculationSummary(count=" + nexusCalculationSummary.getCount() +
                ", amount=" + nexusCalculationSummary.getAmount() + ")";

        // When
        String actualString = nexusCalculationSummary.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameNexusCalculationSummary_ReturnsTrue() {
        // Given
        NexusCalculationSummary givenNexusCalculationSummary = createNexusCalculationSummary();

        // When
        boolean isEquals = nexusCalculationSummary.equals(givenNexusCalculationSummary);

        // Then
        assertTrue(isEquals);
    }

}