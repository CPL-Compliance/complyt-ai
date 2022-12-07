package com.complyt.domain.sales_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxRateTest {

    private final float rate = 0.5f;
    private SalesTaxRate salesTaxRate;

    private SalesTaxRate createSalesTaxRates() {
        return new SalesTaxRate(rate, rate, rate, rate, rate, rate);
    }

    @BeforeEach
    void setup() {
        salesTaxRate = createSalesTaxRates();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "SalesTaxRate(cityDistrictRate=" + rate +
                ", cityRate=" + rate +
                ", countyDistrictRate=" + rate +
                ", countyRate=" + rate +
                ", stateRate=" + rate +
                ", taxRate=" + rate + ")";

        // When
        String actualString = salesTaxRate.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTaxRate_ReturnTrue() {
        // Given
        SalesTaxRate givenSalesTaxRate = createSalesTaxRates();

        // When
        boolean isEquals = salesTaxRate.equals(givenSalesTaxRate);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void zeroSalesTaxRate_ReturnSalesTaxRate() {
        // Given + When
        SalesTaxRate givenSalesTaxRate = SalesTaxRate.zeroSalesTaxRate();

        // Then
        assertEquals(0, givenSalesTaxRate.getTaxRate());
        assertEquals(0, givenSalesTaxRate.getStateRate());
        assertEquals(0, givenSalesTaxRate.getCityRate());
        assertEquals(0, givenSalesTaxRate.getCityDistrictRate());
        assertEquals(0, givenSalesTaxRate.getCountyRate());
        assertEquals(0, givenSalesTaxRate.getCountyDistrictRate());
    }

}