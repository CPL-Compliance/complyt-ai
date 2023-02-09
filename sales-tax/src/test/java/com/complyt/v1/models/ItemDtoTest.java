package com.complyt.v1.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemDtoTest {

    private ItemDto itemDto;

    @BeforeEach
    void setup() {
        itemDto = new ItemDto(2000, 4, 8000, "description", "name", "taxCode",
                null, new SalesTaxRateDto(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategoryDto.INTANGIBLE, TaxableCategoryDto.NOT_TAXABLE
        );
    }

    @Test
    void Equals_sameItemDto_ReturnsTrue() {
        // Given
        ItemDto givenItemDto = new ItemDto(2000, 4, 8000, "description", "name", "taxCode",
                null, new SalesTaxRateDto(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategoryDto.INTANGIBLE, TaxableCategoryDto.NOT_TAXABLE
        );

        // When
        boolean isEquals = itemDto.equals(givenItemDto);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ItemDto(unitPrice=" + itemDto.getUnitPrice() +
                ", quantity=" + itemDto.getQuantity() +
                ", totalPrice=" + itemDto.getTotalPrice() +
                ", description=" + itemDto.getDescription() +
                ", name=" + itemDto.getName() +
                ", taxCode=" + itemDto.getTaxCode() +
                ", jurisdictionalSalesTaxRules=" + itemDto.getJurisdictionalSalesTaxRules() +
                ", salesTaxRate=" + itemDto.getSalesTaxRate() +
                ", manualSalesTax=" + itemDto.isManualSalesTax() +
                ", manualSalesTaxRate=" + itemDto.getManualSalesTaxRate() +
                ", tangibleCategory=" + itemDto.getTangibleCategory() +
                ", taxableCategory=" + itemDto.getTaxableCategory() + ")";

        // When
        String actualString = itemDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}