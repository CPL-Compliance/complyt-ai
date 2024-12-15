package com.complyt.domain.gt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComplytGtRatesTest {

    ComplytGtRates complytGtRates;
    GtAddress gtAddress;
    GtRates gtRates;

    @BeforeEach
    void setup() {
        gtAddress = TestUtilities.createCanadaGtAddress();
        gtRates = TestUtilities.createCanadaGtRates();
        complytGtRates = TestUtilities.createCanadaComplytGtRates();
    }

    @Test
    void Equals_SameComplytGtRates_ReturnsTrue() {
        // Given
        ComplytGtRates givenComplytGtRates = new ComplytGtRates(complytGtRates.id(), gtAddress, gtRates);

        // When
        boolean isEquals = complytGtRates.equals(givenComplytGtRates);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "ComplytGtRates[id=" + complytGtRates.id() +
                ", gtAddress=" + complytGtRates.gtAddress() +
                ", gtRates=" + complytGtRates.gtRates() + "]";

        // When
        String actualString = complytGtRates.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}
