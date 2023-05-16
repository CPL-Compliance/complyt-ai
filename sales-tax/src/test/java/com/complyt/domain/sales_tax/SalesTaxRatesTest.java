package com.complyt.domain.sales_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxRatesTest {

    private final float rate = 0.5f;
    private SalesTaxRates salesTaxRates;

    private SalesTaxRates createSalesTaxRates() {
        return new SalesTaxRates(rate, rate, rate, rate, rate, rate);
    }

    @BeforeEach
    void setup() {
        salesTaxRates = createSalesTaxRates();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "SalesTaxRates[cityDistrictRate=" + rate +
                ", cityRate=" + rate +
                ", countyDistrictRate=" + rate +
                ", countyRate=" + rate +
                ", stateRate=" + rate +
                ", taxRate=" + rate + "]";

        // When
        String actualString = salesTaxRates.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTaxRate_ReturnTrue() {
        // Given
        SalesTaxRates givenSalesTaxRate = createSalesTaxRates();

        // When
        boolean isEquals = salesTaxRates.equals(givenSalesTaxRate);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void zeroSalesTaxRate_ReturnSalesTaxRate() {
        // Given + When
        SalesTaxRates givenSalesTaxRate = SalesTaxRates.zeroSalesTaxRate();

        // Then
        assertEquals(0, givenSalesTaxRate.taxRate());
        assertEquals(0, givenSalesTaxRate.stateRate());
        assertEquals(0, givenSalesTaxRate.cityRate());
        assertEquals(0, givenSalesTaxRate.cityDistrictRate());
        assertEquals(0, givenSalesTaxRate.countyRate());
        assertEquals(0, givenSalesTaxRate.countyDistrictRate());
    }

}