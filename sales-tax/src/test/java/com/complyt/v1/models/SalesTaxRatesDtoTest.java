package com.complyt.v1.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxRatesDtoTest {
    private final float rate = 0.5f;
    private SalesTaxRatesDto salesTaxRatesDto;

    private SalesTaxRatesDto createSalesTaxRateDto() {
        return new SalesTaxRatesDto(rate, rate, rate, rate, rate, rate);
    }

    @BeforeEach
    void setup() {
        salesTaxRatesDto = createSalesTaxRateDto();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "SalesTaxRatesDto[cityDistrictRate=" + rate +
                ", cityRate=" + rate + ", countyDistrictRate=" + rate +
                ", countyRate=" + rate + ", stateRate=" + rate + ", taxRate=" + rate + "]";

        // When
        String actualString = salesTaxRatesDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTaxRateDto_ReturnTrue() {
        // Given
        SalesTaxRatesDto givenSalesTaxRateDto = createSalesTaxRateDto();

        // When
        boolean isEquals = salesTaxRatesDto.equals(givenSalesTaxRateDto);

        // Then
        assertTrue(isEquals);
    }

}