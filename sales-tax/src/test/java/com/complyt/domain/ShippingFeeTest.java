package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.v1.model.TangibleCategoryDto;
import com.complyt.v1.model.TaxableCategoryDto;
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
                shippingFeeWithRuleByPercentage.getJurisdictionalSalesTaxRules().getCalculationValue() * shippingFeeWithRuleByPercentage.getSalesTaxRate().getTaxRate();

        // When + Then
        float actualAmount = shippingFeeWithRuleByPercentage.calculateSalesTaxAmount();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void Equals_sameShippingFee_ReturnsTrue() {
        // Given
        ShippingFee givenShippingFee = createShippingFee();

        // When
        boolean expectedBoolean = shippingFee.equals(givenShippingFee);

        // Then
        assertTrue(expectedBoolean);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ShippingFee(manualSalesTax=false, manualSalesTaxRate=0.0, totalPrice=1000.0, jurisdictionalSalesTaxRules=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=false, calculationType=FIXED, description=description, calculationValue=0.0, cities=null), salesTaxRate=SalesTaxRate(cityDistrictRate=0.0, cityRate=0.0, countyDistrictRate=0.0, countyRate=0.0, stateRate=0.0, taxRate=0.0), taxCode=C6S1, taxableCategory=TAXABLE, tangibleCategory=INTANGIBLE)";

        // When
        String actualString = shippingFee.toString();

        // Then
        assertEquals(expectedString,actualString);
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, rules, SalesTaxRate.zeroSalesTaxRate(), "C6S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", 0, null);
    }
}
