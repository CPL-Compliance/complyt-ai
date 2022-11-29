package com.complyt.v1.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaxableCategoryDtoTest {

    @Test
    public void TaxableCategoryDto_GetTaxable_ReturnTaxable() {
        // Given + When
        TaxableCategoryDto taxableCategoryDto = TaxableCategoryDto.TAXABLE;

        // Then
        assertEquals(TaxableCategoryDto.valueOf("TAXABLE"), taxableCategoryDto);
    }

    @Test
    public void TaxableCategoryDto_GetNot_taxable_ReturnNot_taxable() {
        // Given + When
        TaxableCategoryDto taxableCategoryDto = TaxableCategoryDto.NOT_TAXABLE;

        // Then
        assertEquals(TaxableCategoryDto.valueOf("NOT_TAXABLE"), taxableCategoryDto);
    }

}