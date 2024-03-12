package com.complyt.domain.transaction;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemTest {

    private UnitTestUtilities testUtilities;

    private Item item;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        
        item = testUtilities.createItems(true, true)
                .get(0)
                .withSalesTaxRates(new SalesTaxRates(new BigDecimal("0.01"), new BigDecimal("0.01"),
                        new BigDecimal("0.01"), new BigDecimal("0.01"), new BigDecimal("0.01"), null));
    }

    @Test
    void calculateSalesTaxAmount_SalesTaxIsSetManually_ReturnsAmount() {
        // Given
        Item itemWithManualRate = item.withManualSalesTax(true)
                .withManualSalesTaxRate(new BigDecimal("0.5"))
                .withCalculatedTotal(item.getTotalPrice());
        BigDecimal expectedAmount = itemWithManualRate.getManualSalesTaxRate()
                .multiply(itemWithManualRate.getCalculatedTotal());

        // When + Then
        BigDecimal actualAmount = itemWithManualRate.calculateSalesTaxAmount();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculateSalesTaxAmount_SalesTaxIsSetManuallyAndItemHaveDiscount_ReturnsAmount() {
        // Given
        Item itemWithManualRate = item.withManualSalesTax(true)
                .withManualSalesTaxRate(new BigDecimal("0.5"))
                .withDiscount(BigDecimal.valueOf(500))
                .withCalculatedTotal(item.getTotalPrice().subtract(BigDecimal.valueOf(500)));
        BigDecimal expectedAmount = itemWithManualRate.getManualSalesTaxRate()
                .multiply(itemWithManualRate.getCalculatedTotal());

        // When + Then
        BigDecimal actualAmount = itemWithManualRate.calculateSalesTaxAmount();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculateSalesTaxAmount_RuleIsSetToCalculateByPercentage_ReturnsAmount() {
        // Given
        JurisdictionalSalesTaxRules rulesByPercentage = item.getJurisdictionalSalesTaxRules()
                .withTaxable(true)
                .withSpecialTreatment(true)
                .withCalculationType(CalculationType.PERCENTAGE);

        BigDecimal rateAfterPercentageCut = rulesByPercentage.getCalculationValue().multiply(item.getSalesTaxRates().taxRate());
        SalesTaxRates salesTaxRates = item.getSalesTaxRates().withTaxRate(rateAfterPercentageCut);

        Item itemWithRuleByPercentage = item.withJurisdictionalSalesTaxRules(rulesByPercentage)
                .withSalesTaxRates(salesTaxRates)
                .withCalculatedTotal(item.getTotalPrice());

        BigDecimal expectedAmount = itemWithRuleByPercentage.getCalculatedTotal()
                .multiply(itemWithRuleByPercentage.getSalesTaxRates().taxRate());

        // When + Then
        BigDecimal actualAmount = itemWithRuleByPercentage.calculateSalesTaxAmount();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculateSalesTaxAmount_RuleIsSetToCalculateByPercentageAndItemHaveDiscount_ReturnsAmount() {
        // Given
        JurisdictionalSalesTaxRules rulesByPercentage = item.getJurisdictionalSalesTaxRules()
                .withTaxable(true)
                .withSpecialTreatment(true)
                .withCalculationType(CalculationType.PERCENTAGE);
        BigDecimal rateAfterPercentageCut = rulesByPercentage.getCalculationValue().multiply(item.getSalesTaxRates().taxRate());
        SalesTaxRates salesTaxRates = item.getSalesTaxRates().withTaxRate(rateAfterPercentageCut);

        Item itemWithRuleByPercentage = item.withJurisdictionalSalesTaxRules(rulesByPercentage)
                .withSalesTaxRates(salesTaxRates)
                .withDiscount(BigDecimal.valueOf(500))
                .withCalculatedTotal(item.getTotalPrice().subtract(BigDecimal.valueOf(500)));

        BigDecimal expectedAmount = itemWithRuleByPercentage.getCalculatedTotal()
                .multiply(itemWithRuleByPercentage.getSalesTaxRates().taxRate());

        // When + Then
        BigDecimal actualAmount = itemWithRuleByPercentage.calculateSalesTaxAmount();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void Equals_sameItem_ReturnsTrue() {
        // Given
//        SalesTaxRates salesTaxRates = new SalesTaxRates(new BigDecimal("0.01"), new BigDecimal("0.01"), new BigDecimal("0.01"),
//                new BigDecimal("0.01"), new BigDecimal("0.01"), null);
//        JurisdictionalSalesTaxRules rule = new JurisdictionalSalesTaxRules(
//                "California", "CA", true, true, CalculationType.FIXED,
//                "description", new BigDecimal("0.07"), null);
//        Item givenItem = item = new Item(new BigDecimal("2000"), new BigDecimal("4"),
//                new BigDecimal("8000"), "description", "name", "taxCode", rule, salesTaxRates, false, BigDecimal.ZERO, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE);
        //todo: remove
        Item givenItem = testUtilities.createItems(true, true).get(0)
                .withSalesTaxRates(new SalesTaxRates(new BigDecimal("0.01"), new BigDecimal("0.01"),
                        new BigDecimal("0.01"), new BigDecimal("0.01"), new BigDecimal("0.01"), null));

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
                ", salesTaxRates=" + item.getSalesTaxRates() +
                ", manualSalesTax=" + item.isManualSalesTax() +
                ", manualSalesTaxRate=" + item.getManualSalesTaxRate() +
                ", discount=" + item.getDiscount() +
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
}