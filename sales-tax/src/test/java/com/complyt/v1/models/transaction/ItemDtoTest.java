package com.complyt.v1.models.transaction;

import com.complyt.v1.models.TangibleCategoryDto;
import com.complyt.v1.models.TaxableCategoryDto;
import com.complyt.v1.models.sales_tax.SalesTaxRatesDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemDtoTest {

    private ItemDto itemDto;
    private UnitTestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        itemDto = testUtilities.createItemDtos(true, true, true)
                .get(0)
                .withSalesTaxRates(testUtilities.createSalesTaxRatesDto())
                .withGtRates(testUtilities.createGtRatesDto());
    }

    @Test
    void Equals_sameItemDto_ReturnsTrue() {
        // Given
        ItemDto givenItemDto = testUtilities.createItemDtos(true, true, true)
                .get(0)
                .withSalesTaxRates(testUtilities.createSalesTaxRatesDto())
                .withGtRates(testUtilities.createGtRatesDto());

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
                ", calculatedTotal=" + itemDto.calculatedTotal() +
                ", description=" + itemDto.description() +
                ", name=" + itemDto.name() +
                ", taxCode=" + itemDto.taxCode() +
                ", jurisdictionalSalesTaxRules=" + itemDto.jurisdictionalSalesTaxRules() +
                ", salesTaxRates=" + itemDto.salesTaxRates() +
                ", gtRates=" + itemDto.gtRates() +
                ", manualSalesTax=" + itemDto.manualSalesTax() +
                ", manualSalesTaxRate=" + itemDto.manualSalesTaxRate() +
                ", discount=" + itemDto.discount() +
                ", tangibleCategory=" + itemDto.tangibleCategory() +
                ", taxableCategory=" + itemDto.taxableCategory() + "]";

        // When
        String actualString = itemDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}