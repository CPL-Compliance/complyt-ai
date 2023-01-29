package com.complyt.v1.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaxableCategoryDtoTest {

    @Test
    public void TaxableCategoryDto_GetTaxable_ReturnsTaxable() {
        // Given + When
        TaxableCategoryDto taxableCategoryDto = TaxableCategoryDto.TAXABLE;

        // Then
        assertEquals(TaxableCategoryDto.valueOf("TAXABLE"), taxableCategoryDto);
    }

    @Test
    public void TaxableCategoryDto_GetNot_taxable_ReturnsNot_taxable() {
        // Given + When
        TaxableCategoryDto taxableCategoryDto = TaxableCategoryDto.NOT_TAXABLE;

        // Then
        assertEquals(TaxableCategoryDto.valueOf("NOT_TAXABLE"), taxableCategoryDto);
    }

}