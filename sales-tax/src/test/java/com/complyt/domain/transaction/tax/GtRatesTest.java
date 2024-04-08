package com.complyt.domain.transaction.tax;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GtRatesTest {
    private final BigDecimal rate = new BigDecimal("0.2");
    private GtRates gtRates;

    private GtRates createGtRates() {
        return new GtRates(rate, rate, rate.multiply(BigDecimal.valueOf(2)));
    }

    @BeforeEach
    void setup() {
        gtRates = createGtRates();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "GtRates[countryRate=" + rate +
                ", regionRate=" + rate +
                ", taxRate=" + gtRates.taxRate() + "]";

        // When
        String actualString = gtRates.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void getTaxRate_TaxRatesReturned() {
        // Given + When
        BigDecimal expectedTaxRate = BigDecimal.valueOf(0.4);
        BigDecimal actualTaxRate = gtRates.getTaxRate();

        // Then
        Assertions.assertEquals(expectedTaxRate, actualTaxRate);
    }

    @Test
    void Equals_SameGtRates_ReturnTrue() {
        // Given
        GtRates givenGtRates = createGtRates();

        // When
        boolean isEquals = gtRates.equals(givenGtRates);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void zeroGtRates_ReturnGtRates() {
        // Given + When
        GtRates givenGtRates = GtRates.zeroGtRates();

        // Then
        assertEquals(BigDecimal.ZERO, givenGtRates.taxRate());
        assertEquals(BigDecimal.ZERO, givenGtRates.countryRate());
        assertEquals(BigDecimal.ZERO, givenGtRates.regionRate());
    }

}
