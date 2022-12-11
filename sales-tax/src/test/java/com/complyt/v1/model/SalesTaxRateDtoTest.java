package com.complyt.v1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxRateDtoTest {
    private final float rate = 0.5f;
    private SalesTaxRateDto salesTaxRateDto;

    private SalesTaxRateDto createSalesTaxRateDto() {
        return new SalesTaxRateDto(rate, rate, rate, rate, rate, rate);
    }

    @BeforeEach
    void setup() {
        salesTaxRateDto = createSalesTaxRateDto();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "SalesTaxRateDto(cityDistrictRate=" + rate +
                ", cityRate=" + rate + ", countyDistrictRate=" + rate +
                ", countyRate=" + rate + ", stateRate=" + rate + ", taxRate=" + rate + ")";

        // When
        String actualString = salesTaxRateDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTaxRateDto_ReturnTrue() {
        // Given
        SalesTaxRateDto givenSalesTaxRateDto = createSalesTaxRateDto();

        // When
        boolean isEquals = salesTaxRateDto.equals(givenSalesTaxRateDto);

        // Then
        assertTrue(isEquals);
    }

}