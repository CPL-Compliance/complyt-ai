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
        return new SalesTaxRates(rate, rate, rate, rate, rate, rate);
    }

    @BeforeEach
    void setup() {
        salesTaxRates = createSalesTaxRates();
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
        assertEquals(0, givenSalesTaxRate.getTaxRate());
        assertEquals(0, givenSalesTaxRate.getStateRate());
        assertEquals(0, givenSalesTaxRate.getCityRate());
        assertEquals(0, givenSalesTaxRate.getCityDistrictRate());
        assertEquals(0, givenSalesTaxRate.getCountyRate());
        assertEquals(0, givenSalesTaxRate.getCountyDistrictRate());
    }

}