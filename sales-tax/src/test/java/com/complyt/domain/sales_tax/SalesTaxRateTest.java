package com.complyt.domain.sales_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxRateTest {

    private SalesTaxRate salesTaxRate;

    private SalesTaxRate createSalesTaxRates() {
        return new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
    }

    @BeforeEach
    void setup() {
        salesTaxRate = createSalesTaxRates();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "SalesTaxRate(cityDistrictRate=0.5, cityRate=0.5, countyDistrictRate=0.5, countyRate=0.5, stateRate=0.5, taxRate=0.5)";

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