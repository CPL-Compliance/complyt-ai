package com.complyt.v1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxRateDtoTest {
    private SalesTaxRateDto salesTaxRateDto;

    @BeforeEach
    void setup() {
        salesTaxRateDto = createSalesTaxRateDto();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "SalesTaxRateDto(cityDistrictRate=0.5, cityRate=0.5, countyDistrictRate=0.5, countyRate=0.5, stateRate=0.5, taxRate=0.5)";

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
        boolean actualBoolean = salesTaxRateDto.equals(givenSalesTaxRateDto);

        // Then
        assertTrue(actualBoolean);
    }

    private SalesTaxRateDto createSalesTaxRateDto() {
        return new SalesTaxRateDto(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
    }


}