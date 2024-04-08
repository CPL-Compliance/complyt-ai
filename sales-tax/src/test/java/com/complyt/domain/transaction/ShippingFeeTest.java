package com.complyt.domain.transaction;

import com.complyt.domain.TaxRates;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import com.complyt.domain.transaction.tax.GtRates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShippingFeeTest {

    private ShippingFee shippingFee;

    private UnitTestUtilities testUtilities;


    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        shippingFee = createShippingFee();
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules().withCalculationValue(BigDecimal.ZERO);
        JurisdictionalTaxRules jurisdictionalTaxRules = testUtilities.createJurisdictionalTaxRules().withCalculationValue(BigDecimal.ZERO);
        return testUtilities.createShippingFee(false, false, true)
                .withJurisdictionalSalesTaxRules(jurisdictionalSalesTaxRules)
                .withJurisdictionalTaxRules(jurisdictionalTaxRules)
                .withSalesTaxRates(SalesTaxRates.zeroSalesTaxRate())
                .withGtRates(GtRates.zeroGtRates());
    }

    @Test
    void calculateSalesTaxAmount_SalesTaxIsSetManually_ReturnsAmount() {
        // Given
        ShippingFee shippingFeeWithManualRate = shippingFee
                .withManualSalesTax(true)
                .withManualSalesTaxRate(new BigDecimal("0.5"))
                .withCalculatedTotal(shippingFee.getTotalPrice());
        BigDecimal expectedAmount = shippingFeeWithManualRate.getManualSalesTaxRate().multiply(shippingFeeWithManualRate.getTotalPrice());

        // When + Then
        BigDecimal actualAmount = shippingFeeWithManualRate.calculateSalesTaxAmount();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void getCalculatedTotal_calculatedTotalIsNull_ReturnsZero() {
        // Given
        ShippingFee shippingFeeWithNoCalculatedTotal = shippingFee
                .withCalculatedTotal(null);

        BigDecimal expectedAmount = BigDecimal.ZERO;

        // When + Then
        BigDecimal actualAmount = shippingFeeWithNoCalculatedTotal.getCalculatedTotal();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void getCalculatedTotal_calculatedTotalIsNotNull_ReturnsCalulatedTotal() {
        // Given
        ShippingFee shippingFeeWithCalculatedTotal = shippingFee
                .withCalculatedTotal(BigDecimal.valueOf(5));

        BigDecimal expectedAmount = BigDecimal.valueOf(5);

        // When + Then
        BigDecimal actualAmount = shippingFeeWithCalculatedTotal.getCalculatedTotal();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculateSalesTaxAmount_RuleIsSetToCalculateByPercentage_ReturnsAmount() {
        // Given
        JurisdictionalSalesTaxRules rulesByPercentage = shippingFee.getJurisdictionalSalesTaxRules()
                .withTaxable(true).withSpecialTreatment(true).withCalculationType(CalculationType.PERCENTAGE);
        BigDecimal rateAfterPercentageCut = shippingFee.getSalesTaxRates().taxRate().multiply(rulesByPercentage.getCalculationValue());
        SalesTaxRates salesTaxRates = shippingFee.getSalesTaxRates().withTaxRate(rateAfterPercentageCut);
        ShippingFee shippingFeeWithRuleByPercentage = shippingFee.withJurisdictionalSalesTaxRules(rulesByPercentage)
                .withSalesTaxRates(salesTaxRates)
                .withCalculatedTotal(shippingFee.getTotalPrice());

        BigDecimal expectedAmount = shippingFeeWithRuleByPercentage.getTotalPrice()
                .multiply(shippingFeeWithRuleByPercentage.getSalesTaxRates().taxRate());

        // When + Then
        BigDecimal actualAmount = shippingFeeWithRuleByPercentage.calculateSalesTaxAmount();
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
                ", jurisdictionalTaxRules=" + shippingFee.getJurisdictionalTaxRules() +
                ", salesTaxRates=" + shippingFee.getSalesTaxRates() +
                ", gtRates=" + shippingFee.getGtRates() +
                ", taxCode=" + shippingFee.getTaxCode() +
                ", taxableCategory=" + shippingFee.getTaxableCategory() +
                ", tangibleCategory=" + shippingFee.getTangibleCategory() +
                ", calculatedTotal=" + shippingFee.getCalculatedTotal() + ")";

        // When
        String actualString = shippingFee.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void getTotalPrice_TotalPriceIsNull_ReturnsZero() {
        // Given
        ShippingFee shippingFeeWithNullTotalPrice = shippingFee.withTotalPrice(null);

        // When
        BigDecimal actualTotalPrice = shippingFeeWithNullTotalPrice.getTotalPrice();

        // Then
        assertEquals(BigDecimal.ZERO, actualTotalPrice);
    }

    @Test
    void getTotalPrice_TotalPriceIs10_ReturnsBigDecimalOf10() {
        // Given
        ShippingFee shippingFeeWithNullTotalPrice = shippingFee.withTotalPrice(new BigDecimal("10"));

        // When
        BigDecimal actualTotalPrice = shippingFeeWithNullTotalPrice.getTotalPrice();

        // Then
        assertEquals(new BigDecimal("10"), actualTotalPrice);
    }

    @Test
    void getManualTaxRate_ManualTaxRateIsNull_ReturnsZero() {
        // Given
        ShippingFee shippingFeeWithNullManualTaxRate = shippingFee.withManualSalesTaxRate(null);

        // When
        BigDecimal actualTotalPrice = shippingFeeWithNullManualTaxRate.getManualSalesTaxRate();

        // Then
        assertEquals(BigDecimal.ZERO, actualTotalPrice);
    }

    @Test
    void getManualTaxRate_ManualTaxRateIs10_ReturnsBigDecimalOf10() {
        // Given
        ShippingFee shippingFeeWithManualTaxRateOf10 = shippingFee.withManualSalesTaxRate(new BigDecimal("10"));

        // When
        BigDecimal actualTotalPrice = shippingFeeWithManualTaxRateOf10.getManualSalesTaxRate();

        // Then
        assertEquals(new BigDecimal("10"), actualTotalPrice);
    }

    @Test
    void getTaxRate_GtRatesAreSet_ReturnsItemsGtRates(){
        // Given
        GtRates gtRates = testUtilities.createGtRates();
        ShippingFee shippingFeeWithGtRates = shippingFee
                .withGtRates(gtRates)
                .withSalesTaxRates(null);

        // When
        TaxRates actualGtRates = shippingFeeWithGtRates.getTaxRates();

        // Then
        Assertions.assertEquals(gtRates,actualGtRates);
    }
}