package com.complyt.domain.nexus.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaxableCategoryTest {
    @Test
    public void TaxableCategory_GetTaxable_ReturnsTaxable() {
        // Given + When
        TaxableCategory taxableCategory = TaxableCategory.TAXABLE;

        // Then
        assertEquals(TaxableCategory.valueOf("TAXABLE"), taxableCategory);
    }

    @Test
    public void TaxableCategory_GetNot_taxable_ReturnsNot_taxable() {
        // Given + When
        TaxableCategory taxableCategory = TaxableCategory.NOT_TAXABLE;

        // Then
        assertEquals(TaxableCategory.valueOf("NOT_TAXABLE"), taxableCategory);
    }

}