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

    private PhysicalNexusTracker createPhysicalNexusTracker() {
        return new PhysicalNexusTracker(true, localDateTime);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "PhysicalNexusTracker(established=" + physicalNexusTracker.isEstablished() +
                ", establishedDate=" + physicalNexusTracker.getEstablishedDate() + ")";

        // When
        String actualString = physicalNexusTracker.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SamePhysicalNexusTracker_ReturnsTrue() {
        // Given
        PhysicalNexusTracker givenPhysicalNexusTracker = createPhysicalNexusTracker();

        // When
        boolean isEquals = physicalNexusTracker.equals(givenPhysicalNexusTracker);

        // Then
        assertTrue(isEquals);
    }

}