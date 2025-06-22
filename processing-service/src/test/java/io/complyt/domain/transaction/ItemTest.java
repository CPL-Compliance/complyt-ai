package io.complyt.domain.transaction;

import io.complyt.domain.nexus.enums.TangibleCategory;
import io.complyt.domain.nexus.enums.TaxableCategory;
import io.complyt.domain.sales_tax.SalesTaxRates;
import io.complyt.domain.sales_tax.product_classification.CalculationType;
import io.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemTest {

    private Item item;

    private UnitTestUtilities testUtilities;

   

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        SalesTaxRates salesTaxRates = new SalesTaxRates(null, new BigDecimal("0.01"), new BigDecimal("0.01"),
                new BigDecimal("0.01"),null, null, null, null, new BigDecimal("0.01"));
        JurisdictionalSalesTaxRules rule = new JurisdictionalSalesTaxRules(
                "California", "CA", true, true, CalculationType.FIXED,
                "description", new BigDecimal("0.07"), null);
        item = testUtilities.createItemsWithSalesTaxRate(true, false, true)
                .get(0).withTaxableCategory(TaxableCategory.NOT_TAXABLE)
                .withTangibleCategory(TangibleCategory.INTANGIBLE)
                .withJurisdictionalTaxRules(testUtilities.createJurisdictionalTaxRules())
                .withGtRates(testUtilities.createGtRates());
    }

    @Test
    void Equals_sameItem_ReturnsTrue() {
        // Given
        Item givenItem = item.withDescription(item.getDescription());

        // When
        boolean isEquals = item.equals(givenItem);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "Item(unitPrice=" + item.getUnitPrice() +
                ", quantity=" + item.getQuantity() +
                ", totalPrice=" + item.getTotalPrice() +
                ", calculatedTotal=" + item.getCalculatedTotal() +
                ", description=" + item.getDescription() +
                ", name=" + item.getName() +
                ", taxCode=" + item.getTaxCode() +
                ", jurisdictionalSalesTaxRules=" + item.getJurisdictionalSalesTaxRules() +
                ", jurisdictionalTaxRules=" + item.getJurisdictionalTaxRules() +
                ", salesTaxRates=" + item.getSalesTaxRates() +
                ", gtRates=" + item.getGtRates() +
                ", manualSalesTax=" + item.isManualSalesTax() +
                ", manualSalesTaxRate=" + item.getManualSalesTaxRate() +
                ", discount=" + item.getDiscount() +
                ", relativeTransactionDiscount=" + item.getRelativeTransactionDiscount() +
                ", tangibleCategory=" + item.getTangibleCategory() +
                ", taxableCategory=" + item.getTaxableCategory() + ")";

        // When
        String actualString = item.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void getTotalPrice_TotalPriceIsNullButUnitPriceIs2QuantityIs10_ReturnsBigDecimalOf20() {
        // Given
        Item itemWithNullTotalPrice = item.withTotalPrice(null)
                .withUnitPrice(BigDecimal.valueOf(2))
                .withQuantity(BigDecimal.valueOf(10));

        // When
        BigDecimal actualTotalPrice = itemWithNullTotalPrice.getTotalPrice();

        // Then
        assertEquals(BigDecimal.valueOf(20), actualTotalPrice);
    }

    @Test
    void getTotalPrice_TotalPriceIs10_ReturnsBigDecimalOf10() {
        // Given
        Item itemWithTotalPriceOf10 = item.withTotalPrice(new BigDecimal("10"));

        // When
        BigDecimal actualTotalPrice = itemWithTotalPriceOf10.getTotalPrice();

        // Then
        assertEquals(new BigDecimal("10"), actualTotalPrice);
    }

    @Test
    void getManualTaxRate_ManualTaxRateIsNull_ReturnsZero() {
        // Given
        Item itemWithNullManualTaxRate = item.withManualSalesTaxRate(null);

        // When
        BigDecimal actualManualSalesTaxRate = itemWithNullManualTaxRate.getManualSalesTaxRate();

        // Then
        assertEquals(BigDecimal.ZERO, actualManualSalesTaxRate);
    }

    @Test
    void getManualTaxRate_ManualTaxRateIs10_ReturnsBigDecimalOf10() {
        // Given
        Item itemWithManualTaxRateOf10 = item.withManualSalesTaxRate(new BigDecimal("10"));

        // When
        BigDecimal actualManualSalesTaxRate = itemWithManualTaxRateOf10.getManualSalesTaxRate();

        // Then
        assertEquals(new BigDecimal("10"), actualManualSalesTaxRate);
    }

    @Test
    void getQuantity_QuantityIsNull_ReturnsZero() {
        // Given
        Item itemWithNullQuantity = item.withQuantity(null);

        // When
        BigDecimal actualQuantity = itemWithNullQuantity.getQuantity();

        // Then
        assertEquals(BigDecimal.ZERO, actualQuantity);
    }

    @Test
    void getQuantity_QuantityIs10_ReturnsBigDecimalOf10() {
        // Given
        Item itemWithQuantityOf10 = item.withQuantity(new BigDecimal("10"));

        // When
        BigDecimal actualQuantity = itemWithQuantityOf10.getQuantity();

        // Then
        assertEquals(new BigDecimal("10"), actualQuantity);
    }

    @Test
    void getUnitPrice_UnitPriceIsNull_ReturnsZero() {
        // Given
        Item itemWithNullUnitPrice = item.withUnitPrice(null);

        // When
        BigDecimal actualUnitPrice = itemWithNullUnitPrice.getUnitPrice();

        // Then
        assertEquals(BigDecimal.ZERO, actualUnitPrice);
    }

    @Test
    void getUnitPrice_UnitPriceIs10_ReturnsBigDecimalOf10() {
        // Given
        Item itemWithUnitPriceOf10 = item.withUnitPrice(new BigDecimal("10"));

        // When
        BigDecimal actualUnitPrice = itemWithUnitPriceOf10.getUnitPrice();

        // Then
        assertEquals(new BigDecimal("10"), actualUnitPrice);
    }

    @Test
    void getCalculatedTotal_calculatedTotalIsNull_ReturnsZero() {
        // Given
        Item itemWithNoCalculatedTotal = item
                .withCalculatedTotal(null);

        BigDecimal expectedAmount = BigDecimal.ZERO;

        // When + Then
        BigDecimal actualAmount = itemWithNoCalculatedTotal.getCalculatedTotal();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void getCalculatedTotal_calculatedTotalIsNotNull_ReturnsCalulatedTotal() {
        // Given
        Item itemWithCalculatedTotal = item
                .withCalculatedTotal(BigDecimal.valueOf(5));

        BigDecimal expectedAmount = BigDecimal.valueOf(5);

        // When + Then
        BigDecimal actualAmount = itemWithCalculatedTotal.getCalculatedTotal();
        assertEquals(expectedAmount, actualAmount);
    }

}