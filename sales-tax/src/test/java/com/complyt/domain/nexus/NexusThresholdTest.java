package com.complyt.domain.nexus;

import com.complyt.domain.nexus.enums.Definition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NexusThresholdTest {
    private NexusThreshold nexusThreshold;

    @BeforeEach
    void setup() {
        nexusThreshold = createNexusThreshold();
    }

    private NexusThreshold createNexusThreshold() {

        return new NexusThreshold(BigDecimal.ZERO, 0, Definition.COUNT);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "NexusThreshold(amount=" + nexusThreshold.getAmount() +
                ", count=" + nexusThreshold.getCount() + ", definition=" + nexusThreshold.getDefinition() + ")";

        // When
        String actualString = nexusThreshold.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameNexusThreshold_ReturnTrue() {
        // Given
        NexusThreshold givenNexusThreshold = createNexusThreshold();

        // When
        boolean isEquals = nexusThreshold.equals(givenNexusThreshold);

        // Then
        assertTrue(isEquals);
    }

}