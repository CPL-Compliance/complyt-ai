package com.example.complyt.domain.gt;

import com.complyt.domain.gt.GtRates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GtRatesTest {
    GtRates gtRates;

    @BeforeEach
    void setup() {
        gtRates = TestUtilities.createCanadaGtRates();
    }

    @Test
    void Equals_SameComplytGtRates_ReturnsTrue() {
        // Given
        GtRates givenGtRates = new GtRates(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.0975), BigDecimal.valueOf(0.14975));

        // When
        boolean isEquals = gtRates.equals(givenGtRates);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "GtRates[countryRate=" + gtRates.countryRate() +
                ", regionRate=" + gtRates.regionRate() +
                ", taxRate=" + gtRates.taxRate() + "]";

        // When
        String actualString = gtRates.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}
