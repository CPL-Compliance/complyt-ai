package com.complyt.domain.nexus;

import com.complyt.domain.nexus.enums.Definition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NexusThresholdTest {
    private NexusThreshold nexusThreshold;

    @BeforeEach
    void setup() {
        nexusThreshold = createNexusThreshold();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "NexusThreshold(amount=0.0, count=0, definition=COUNT)";

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
        boolean actualBoolean = nexusThreshold.equals(givenNexusThreshold);

        // Then
        assertTrue(actualBoolean);
    }

    private NexusThreshold createNexusThreshold() {
        return new NexusThreshold(0, 0, Definition.COUNT);
    }

}