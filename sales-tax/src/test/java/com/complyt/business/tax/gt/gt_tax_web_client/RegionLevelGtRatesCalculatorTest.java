package com.complyt.business.tax.gt.gt_tax_web_client;

import com.complyt.business.tax.gt.RegionLevelGtRatesCalculator;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.SubJurisdictionalTaxRules;
import com.complyt.domain.transaction.tax.GtRates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegionLevelGtRatesCalculatorTest {
    RegionLevelGtRatesCalculator regionLevelGtRatesCalculator;
    UnitTestUtilities testUtilities;
    SubJurisdictionalTaxRules regionSalesTaxRules;
    GtRates gtRates;

    @BeforeEach
    void setUp() {
        regionLevelGtRatesCalculator = new RegionLevelGtRatesCalculator();
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
        gtRates = testUtilities.createGtRates();
        regionSalesTaxRules = testUtilities.createRegionSalesTaxRules();
    }

    @Test
    void calculate_RegionRuleIsNotTaxable_ReturnsZeroRegionRate() {
        // Given
        SubJurisdictionalTaxRules nonTaxableRegionRule = regionSalesTaxRules.withTaxable(false);
        GtRates expectedSalesTaxRate = gtRates.withRegionRate(BigDecimal.ZERO);

        // When
        GtRates actualSalesTaxRate = regionLevelGtRatesCalculator.calculate(nonTaxableRegionRule, gtRates);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_RegionRuleIsTaxable_ReturnsTaxableRegionRate() {
        // Given
        SubJurisdictionalTaxRules taxableRegionRule = regionSalesTaxRules.withTaxable(true);

        // When
        GtRates actualSalesTaxRate = regionLevelGtRatesCalculator.calculate(taxableRegionRule, gtRates);

        // Then
        assertEquals(gtRates, actualSalesTaxRate);
    }

    @Test
    void calculate_RegionRuleIsTaxable_ReturnsTaxableRegionRateWithRemainingRatesAsZero() {
        // Given
        SubJurisdictionalTaxRules taxableRegionRule = regionSalesTaxRules.withTaxable(true);
        GtRates zeroSalesTaxRateWithRegionRate = GtRates.zeroGtRates().withRegionRate(gtRates.regionRate());
        GtRates expectedSalesTaxRate = zeroSalesTaxRateWithRegionRate.withTaxRate(gtRates.regionRate());

        // When
        GtRates actualSalesTaxRate = regionLevelGtRatesCalculator.calculate(taxableRegionRule, zeroSalesTaxRateWithRegionRate);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_RegionRuleIsNonTaxable_ReturnsZeroRates() {
        // Given
        SubJurisdictionalTaxRules taxableRegionRule = regionSalesTaxRules.withTaxable(false);
        GtRates zeroSalesTaxRateWithRegionRate = GtRates.zeroGtRates().withRegionRate(gtRates.regionRate());
        GtRates expectedSalesTaxRate = GtRates.zeroGtRates();

        // When
        GtRates actualSalesTaxRate = regionLevelGtRatesCalculator.calculate(taxableRegionRule, zeroSalesTaxRateWithRegionRate);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_SpecialTreatmentByFixed_ReturnsFixedRegionRate() {
        // Given
        SubJurisdictionalTaxRules taxableRegionRule = regionSalesTaxRules.withTaxable(true).withSpecialTreatment(true);
        BigDecimal newRegionRate = taxableRegionRule.getCalculationValue();
        GtRates gtRates = testUtilities.createGtRates()
                .withRegionRate(newRegionRate);
        BigDecimal taxRate = newRegionRate.add(gtRates.countryRate());

        GtRates expectedSalesTaxRate = gtRates.withTaxRate(taxRate).withRegionRate(newRegionRate);
        System.out.println("gtRates: " + gtRates);
        // When
        GtRates actualSalesTaxRate = regionLevelGtRatesCalculator.calculate(taxableRegionRule, gtRates);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_SpecialTreatmentByPercentage_ReturnsPercentageRegionRate() {
        // Given
        SubJurisdictionalTaxRules taxableRegionRule = regionSalesTaxRules.withTaxable(true)
                .withSpecialTreatment(true)
                .withCalculationType(CalculationType.PERCENTAGE);
        GtRates originalSalesTaxRate = testUtilities.createGtRates().withRegionRate(new BigDecimal("0.05"));
        BigDecimal newRegionTaxRate = originalSalesTaxRate.regionRate().multiply(taxableRegionRule.getCalculationValue());
        BigDecimal taxRate = newRegionTaxRate.add(originalSalesTaxRate.countryRate());

        GtRates expectedSalesTaxRate = originalSalesTaxRate.withTaxRate(taxRate).withRegionRate(newRegionTaxRate);

        // When
        GtRates actualSalesTaxRate = regionLevelGtRatesCalculator.calculate(taxableRegionRule, originalSalesTaxRate);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_NullRegionSalesTaxRulesPassed_ThrowsException() {
        // Given
        SubJurisdictionalTaxRules nullRegionSalesTaxRules = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            regionLevelGtRatesCalculator.calculate(nullRegionSalesTaxRules, gtRates);
        });

        assertEquals(nullPointerException.getMessage(), "regionJurisdictionalTaxRules is marked non-null but is null");
    }

    @Test
    void calculate_NullSalesTaxRatePassed_ThrowsException() {
        // Given
        GtRates nullSalesTaxRate = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            regionLevelGtRatesCalculator.calculate(regionSalesTaxRules, nullSalesTaxRate);
        });

        assertEquals(nullPointerException.getMessage(), "originalGtRate is marked non-null but is null");
    }

}

