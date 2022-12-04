package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingFeeTest {

    private ShippingFee shippingFee;

    @BeforeEach
    void setUp() {
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.05f);
        JurisdictionalSalesTaxRules rules = new JurisdictionalSalesTaxRules(
                "California", "CA", true, true, CalculationType.FIXED,
                "description", 0.07f, null);
        shippingFee = new ShippingFee(false, 0, 1000, rules, salesTaxRate, "taxCode", TaxableCategory.NOT_TAXABLE, TangibleCategory.INTANGIBLE);
    }

    @Test
    void calculateSalesTaxAmount_SalesTaxIsSetManually_ReturnsAmount() {
        // Given
        ShippingFee shippingFeeWithManualRate = shippingFee.withManualSalesTax(true).withManualSalesTaxRate(0.5f);
        float expectedAmount = shippingFeeWithManualRate.getManualSalesTaxRate() * shippingFeeWithManualRate.getTotalPrice();

        // When + Then
        float actualAmount = shippingFeeWithManualRate.calculateSalesTaxAmount();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculateSalesTaxAmount_RuleIsSetToCalculateByPercentage_ReturnsAmount() {
        // Given
        JurisdictionalSalesTaxRules rulesByPercentage = shippingFee.getJurisdictionalSalesTaxRules()
                .withTaxable(true).withSpecialTreatment(true).withCalculationType(CalculationType.PERCENTAGE);

        ShippingFee shippingFeeWithRuleByPercentage = shippingFee.withJurisdictionalSalesTaxRules(rulesByPercentage);
        float expectedAmount = shippingFeeWithRuleByPercentage.getTotalPrice() *
                shippingFeeWithRuleByPercentage.getSalesTaxRate().getTaxRate();

        // When + Then
        float actualAmount = shippingFeeWithRuleByPercentage.calculateSalesTaxAmount();
        assertEquals(expectedAmount, actualAmount);
    }
}
