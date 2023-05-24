package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        SalesTaxRates salesTaxRates = new SalesTaxRates(0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.05f);
        JurisdictionalSalesTaxRules rule = new JurisdictionalSalesTaxRules(
                "California", "CA", true, true, CalculationType.FIXED,
                "description", 0.07f, null);
        item = new Item(2000, 4, 8000, "description", "name", "taxCode", rule, salesTaxRates, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE);
    }

    @Test
    void calculateSalesTaxAmount_SalesTaxIsSetManually_ReturnsAmount() {
        // Given
        Item itemWithManualRate = item.withManualSalesTax(true).withManualSalesTaxRate(0.5f);
        float expectedAmount = itemWithManualRate.getManualSalesTaxRate() * itemWithManualRate.getTotalPrice();

        // When + Then
        float actualAmount = itemWithManualRate.calculateSalesTaxAmount();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculateSalesTaxAmount_RuleIsSetToCalculateByPercentage_ReturnsAmount() {
        // Given
        JurisdictionalSalesTaxRules rulesByPercentage = item.getJurisdictionalSalesTaxRules()
                .withTaxable(true).withSpecialTreatment(true).withCalculationType(CalculationType.PERCENTAGE);
        float rateAfterPercentageCut = rulesByPercentage.getCalculationValue() * item.getSalesTaxRates().taxRate();
        SalesTaxRates salesTaxRates = item.getSalesTaxRates().withTaxRate(rateAfterPercentageCut);

        Item itemWithRuleByPercentage = item.withJurisdictionalSalesTaxRules(rulesByPercentage)
                .withSalesTaxRates(salesTaxRates);

        float expectedAmount = itemWithRuleByPercentage.getTotalPrice()
                * itemWithRuleByPercentage.getSalesTaxRates().taxRate();

        // When + Then
        float actualAmount = itemWithRuleByPercentage.calculateSalesTaxAmount();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void Equals_sameItem_ReturnsTrue() {
        // Given
        SalesTaxRates salesTaxRates = new SalesTaxRates(0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.05f);
        JurisdictionalSalesTaxRules rule = new JurisdictionalSalesTaxRules(
                "California", "CA", true, true, CalculationType.FIXED,
                "description", 0.07f, null);
        Item givenItem = item = new Item(2000, 4, 8000, "description", "name", "taxCode", rule, salesTaxRates, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE);

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