package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        SalesTaxRates salesTaxRates = new SalesTaxRates(new BigDecimal("0.01"), new BigDecimal("0.01"),
                new BigDecimal("0.01"), new BigDecimal("0.01"), new BigDecimal("0.01"), null);
        JurisdictionalSalesTaxRules rule = new JurisdictionalSalesTaxRules(
                "California", "CA", true, true, CalculationType.FIXED,
                "description", new BigDecimal("0.07"), null);
        item = new Item(new BigDecimal("2000"), new BigDecimal("4"), new BigDecimal("8000"), "description", "name", "taxCode", rule, salesTaxRates, false, BigDecimal.ZERO, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE);
    }

    @Test
    void calculateSalesTaxAmount_SalesTaxIsSetManually_ReturnsAmount() {
        // Given
        Item itemWithManualRate = item.withManualSalesTax(true).withManualSalesTaxRate(new BigDecimal("0.5"));
        BigDecimal expectedAmount = itemWithManualRate.getManualSalesTaxRate().multiply(itemWithManualRate.getTotalPrice());

        // When + Then
        BigDecimal actualAmount = itemWithManualRate.calculateSalesTaxAmount();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculateSalesTaxAmount_RuleIsSetToCalculateByPercentage_ReturnsAmount() {
        // Given
        JurisdictionalSalesTaxRules rulesByPercentage = item.getJurisdictionalSalesTaxRules()
                .withTaxable(true).withSpecialTreatment(true).withCalculationType(CalculationType.PERCENTAGE);
        BigDecimal rateAfterPercentageCut = rulesByPercentage.getCalculationValue().multiply(item.getSalesTaxRates().taxRate());
        SalesTaxRates salesTaxRates = item.getSalesTaxRates().withTaxRate(rateAfterPercentageCut);

        Item itemWithRuleByPercentage = item.withJurisdictionalSalesTaxRules(rulesByPercentage)
                .withSalesTaxRates(salesTaxRates);

        BigDecimal expectedAmount = itemWithRuleByPercentage.getTotalPrice()
                .multiply(itemWithRuleByPercentage.getSalesTaxRates().taxRate());

        // When + Then
        BigDecimal actualAmount = itemWithRuleByPercentage.calculateSalesTaxAmount();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void Equals_sameItem_ReturnsTrue() {
        // Given
        SalesTaxRates salesTaxRates = new SalesTaxRates(new BigDecimal("0.01"), new BigDecimal("0.01"), new BigDecimal("0.01"),
                new BigDecimal("0.01"), new BigDecimal("0.01"), null);
        JurisdictionalSalesTaxRules rule = new JurisdictionalSalesTaxRules(
                "California", "CA", true, true, CalculationType.FIXED,
                "description", new BigDecimal("0.07"), null);
        Item givenItem = item = new Item(new BigDecimal("2000"), new BigDecimal("4"), new BigDecimal("8000"), "description", "name", "taxCode", rule, salesTaxRates, false, BigDecimal.ZERO, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE);

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
                ", description=" + item.getDescription() +
                ", name=" + item.getName() +
                ", taxCode=" + item.getTaxCode() +
                ", jurisdictionalSalesTaxRules=" + item.getJurisdictionalSalesTaxRules() +
                ", salesTaxRates=" + item.getSalesTaxRates() +
                ", manualSalesTax=" + item.isManualSalesTax() +
                ", manualSalesTaxRate=" + item.getManualSalesTaxRate() +
                ", tangibleCategory=" + item.getTangibleCategory() +
                ", taxableCategory=" + item.getTaxableCategory() + ")";

        // When
        String actualString = item.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}