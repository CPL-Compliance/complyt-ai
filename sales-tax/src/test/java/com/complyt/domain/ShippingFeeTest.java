package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShippingFeeTest {

    private ShippingFee shippingFee;

    @BeforeEach
    void setUp() {
        shippingFee = createShippingFee();
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, rules, SalesTaxRate.zeroSalesTaxRate(), "C6S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", 0, null);
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
        float rateAfterPercentageCut = shippingFee.getSalesTaxRate().getTaxRate() * rulesByPercentage.getCalculationValue();
        SalesTaxRate salesTaxRate = shippingFee.getSalesTaxRate().withTaxRate(rateAfterPercentageCut);
        ShippingFee shippingFeeWithRuleByPercentage = shippingFee.withJurisdictionalSalesTaxRules(rulesByPercentage)
                .withSalesTaxRate(salesTaxRate);

        float expectedAmount = shippingFeeWithRuleByPercentage.getTotalPrice() *
                shippingFeeWithRuleByPercentage.getSalesTaxRate().getTaxRate();

        // When + Then
        float actualAmount = shippingFeeWithRuleByPercentage.calculateSalesTaxAmount();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void Equals_sameShippingFee_ReturnsTrue() {
        // Given
        ShippingFee givenShippingFee = createShippingFee();

        // When
        boolean isEquals = shippingFee.equals(givenShippingFee);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "ShippingFee(manualSalesTax=" + shippingFee.isManualSalesTax() +
                ", manualSalesTaxRate=" + shippingFee.getManualSalesTaxRate() +
                ", totalPrice=" + shippingFee.getTotalPrice() +
                ", jurisdictionalSalesTaxRules=" + shippingFee.getJurisdictionalSalesTaxRules() +
                ", salesTaxRate=" + shippingFee.getSalesTaxRate() +
                ", taxCode=" + shippingFee.getTaxCode() +
                ", taxableCategory=" + shippingFee.getTaxableCategory() +
                ", tangibleCategory=" + shippingFee.getTangibleCategory() + ")";

        // When
        String actualString = shippingFee.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}
