package com.complyt.v1.model;

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
        String expectedString = "ItemDto(unitPrice=2000.0, quantity=4, totalPrice=8000.0" +
                ", description=description, name=name, taxCode=taxCode" +
                ", jurisdictionalSalesTaxRules=null, salesTaxRate=" + itemDto.getSalesTaxRate() +
                ", manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=INTANGIBLE" +
                ", taxableCategory=NOT_TAXABLE)";

        // When
        String actualString = itemDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}