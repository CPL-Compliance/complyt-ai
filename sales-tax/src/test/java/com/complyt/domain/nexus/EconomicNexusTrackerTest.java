package com.complyt.domain.nexus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EconomicNexusTrackerTest {
    private EconomicNexusTracker economicNexusTracker;

    private LocalDateTime localDateTime;

    @BeforeEach
    void setup() {
        localDateTime = LocalDateTime.now();
        economicNexusTracker = createEconomicNexusTracker();
    }

    private EconomicNexusTracker createEconomicNexusTracker() {
        return new EconomicNexusTracker(true, localDateTime);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "EconomicNexusTracker(established=true, establishedDate=" + localDateTime + ")";

        // When
        String actualString = economicNexusTracker.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameEconomicNexusTracker_ReturnsTrue() {
        // Given
        EconomicNexusTracker givenEconomicNexusTracker = createEconomicNexusTracker();

        // When
        boolean isEquals = economicNexusTracker.equals(givenEconomicNexusTracker);

        // Then
        assertTrue(isEquals);
    }

}