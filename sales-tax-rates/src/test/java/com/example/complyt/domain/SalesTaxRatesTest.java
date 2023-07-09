package com.example.complyt.domain;

import com.complyt.domain.SalesTaxRates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxRatesTest {

    private final float rate = 0.5f;
    private SalesTaxRates salesTaxRates;

    private SalesTaxRates createSalesTaxRates() {
        return new SalesTaxRates(rate, rate, rate, rate, rate, null);
    }

    @BeforeEach
    void setup() {
        salesTaxRates = createSalesTaxRates();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "SalesTaxRates[cityRate=" + rate +
                ", countyRate=" + rate +
                ", stateRate=" + rate +
                ", taxRate=" + rate +
                ", combinedDistrictRate=" + rate +
                ", ratesMetaData=" + salesTaxRates.ratesMetaData() +"]";

        // When
        String actualString = salesTaxRates.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTaxRate_ReturnTrue() {
        // Given
        SalesTaxRates givenSalesTaxRates = createSalesTaxRates();

        // When
        boolean isEquals = salesTaxRates.equals(givenSalesTaxRates);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void zeroSalesTaxRate_ReturnSalesTaxRate() {
        // Given + When
        SalesTaxRates givenSalesTaxRate = SalesTaxRates.zeroSalesTaxRates();

        // Then
        assertEquals(0, givenSalesTaxRate.taxRate());
        assertEquals(0, givenSalesTaxRate.stateRate());
        assertEquals(0, givenSalesTaxRate.cityRate());
        assertEquals(0, givenSalesTaxRate.combinedDistrictRate());
        assertEquals(0, givenSalesTaxRate.countyRate());
    }

}