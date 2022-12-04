package com.complyt.domain.nexus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhysicalNexusTrackerTest {
    private PhysicalNexusTracker physicalNexusTracker;

    private LocalDateTime localDateTime;

    @BeforeEach
    void setup() {
        localDateTime = LocalDateTime.now();
        physicalNexusTracker = createPhysicalNexusTracker();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "PhysicalNexusTracker(established=true, establishedDate=" + localDateTime + ")";

        // When
        String actualString = physicalNexusTracker.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SamePhysicalNexusTracker_ReturnTrue() {
        // Given
        PhysicalNexusTracker givenPhysicalNexusTracker = createPhysicalNexusTracker();

        // When
        boolean actualBoolean = physicalNexusTracker.equals(givenPhysicalNexusTracker);

        // Then
        assertTrue(actualBoolean);
    }

    private PhysicalNexusTracker createPhysicalNexusTracker() {
        return new PhysicalNexusTracker(true, localDateTime);
    }

}