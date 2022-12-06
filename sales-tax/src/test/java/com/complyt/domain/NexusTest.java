package com.complyt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NexusTest {

    private Nexus nexus;
    private LocalDateTime nexusDate;

    @BeforeEach
    void setup() {
        nexusDate = LocalDateTime.now();
        nexus = new Nexus(nexusDate);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "Nexus(taxableDate=" + nexusDate + ")";

        // When
        String actualString = nexus.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameNexus_ReturnTrue() {
        // Given
        Nexus givenNexus = new Nexus(nexusDate);

        // When
        boolean isEquals = nexus.equals(givenNexus);

        // Then
        assertTrue(isEquals);
    }
}