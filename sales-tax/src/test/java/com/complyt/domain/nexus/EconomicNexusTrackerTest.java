package com.complyt.domain.nexus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EconomicNexusTrackerTest {
    private EconomicNexusTracker economicNexusTracker;

    private LocalDateTime localDateTime;

    @BeforeEach
    void setup() {
        localDateTime = LocalDateTime.now();
        economicNexusTracker = createEconomicNexusTracker();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "EconomicNexusTracker(established=true, establishedDate=" + localDateTime + ")";

        // When
        String actualString = economicNexusTracker.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameEconomicNexusTracker_ReturnTrue() {
        // Given
        EconomicNexusTracker givenEconomicNexusTracker = createEconomicNexusTracker();

        // When
        boolean actualBoolean = economicNexusTracker.equals(givenEconomicNexusTracker);

        // Then
        assertTrue(actualBoolean);
    }

    private EconomicNexusTracker createEconomicNexusTracker() {
        return new EconomicNexusTracker(true, localDateTime);
    }

}