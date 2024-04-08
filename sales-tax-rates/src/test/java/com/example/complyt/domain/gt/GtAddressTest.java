package com.example.complyt.domain.gt;

import com.complyt.domain.gt.GtAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GtAddressTest {
    GtAddress gtAddress;

    @BeforeEach
    void setup() {
        gtAddress = TestUtilities.createCanadaGtAddress();
    }

    @Test
    void Equals_SameComplytGtRates_ReturnsTrue() {
        // Given
        GtAddress givenGtAddress = new GtAddress("Canada", "Quebec");

        // When
        boolean isEquals = gtAddress.equals(givenGtAddress);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "GtAddress[country=" + gtAddress.country() +
                ", region=" + gtAddress.region() + "]";

        // When
        String actualString = gtAddress.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}