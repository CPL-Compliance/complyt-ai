package com.complyt.v1.models;

import com.complyt.v1.models.sales_tax.SalesTaxRatesDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemDtoTest {

    private ItemDto itemDto;

    @BeforeEach
    void setup() {
        itemDto = new ItemDto(new BigDecimal("2000"), new BigDecimal("4"), new BigDecimal("8000"), "description", "name", "taxCode",
                null, new SalesTaxRatesDto(new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), null), false, BigDecimal.ZERO, TangibleCategoryDto.INTANGIBLE, TaxableCategoryDto.NOT_TAXABLE
        );
    }

    @Test
    void Equals_sameItemDto_ReturnsTrue() {
        // Given
        ItemDto givenItemDto = new ItemDto(new BigDecimal("2000"), new BigDecimal("4"), new BigDecimal(" 8000"), "description", "name", "taxCode",
                null, new SalesTaxRatesDto(new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), null), false, BigDecimal.ZERO, TangibleCategoryDto.INTANGIBLE, TaxableCategoryDto.NOT_TAXABLE
        );

        // When
        boolean isEquals = itemDto.equals(givenItemDto);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ItemDto[unitPrice=" + itemDto.unitPrice() +
                ", quantity=" + itemDto.quantity() +
                ", totalPrice=" + itemDto.totalPrice() +
                ", description=" + itemDto.description() +
                ", name=" + itemDto.name() +
                ", taxCode=" + itemDto.taxCode() +
                ", jurisdictionalSalesTaxRules=" + itemDto.jurisdictionalSalesTaxRules() +
                ", salesTaxRates=" + itemDto.salesTaxRates() +
                ", manualSalesTax=" + itemDto.manualSalesTax() +
                ", manualSalesTaxRate=" + itemDto.manualSalesTaxRate() +
                ", tangibleCategory=" + itemDto.tangibleCategory() +
                ", taxableCategory=" + itemDto.taxableCategory() + "]";

        // When
        String actualString = itemDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}