package com.complyt.business.tax.gt.gt_tax_web_client;

import com.complyt.business.tax.gt.CountryLevelGtRatesCalculator;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CountryLevelGtRatesCalculatorTest {

    CountryLevelGtRatesCalculator countryLevelGtRatesCalculator;
    UnitTestUtilities testUtilities;
    JurisdictionalTaxRules jurisdictionalTaxRules;
    GtRates gtRates;

    @BeforeEach
    void setUp() {
        countryLevelGtRatesCalculator = new CountryLevelGtRatesCalculator();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        gtRates = testUtilities.createGtRates();
        jurisdictionalTaxRules = testUtilities.createJurisdictionalTaxRules();
    }

    @Test
    void calculateGtRates_StateLevelNotTaxable_ReturnsZeroTaxRate() {
        // Given
        GtRates expectedGtRate = GtRates.zeroGtRates();
        JurisdictionalTaxRules givenJurisdictionalTaxRules = jurisdictionalTaxRules.withTaxable(false);

        // When
        GtRates actualGtRates = countryLevelGtRatesCalculator.calculate(givenJurisdictionalTaxRules, gtRates);

        // Then
        assertEquals(expectedGtRate, actualGtRates);
    }

    @Test
    void calculateGtRates_StateLevelHasNoSpecialTreatment_ReturnsSameTaxRate() {
        // Given
        JurisdictionalTaxRules givenJurisdictionalSalesTaxRules = jurisdictionalTaxRules.withSpecialTreatment(false);

        // When
        GtRates actualGtRates = countryLevelGtRatesCalculator.calculate(givenJurisdictionalSalesTaxRules, gtRates);

        // Then
        assertEquals(gtRates, actualGtRates);
    }

    @Test
    void calculateGtRates_FixedCalculation_ReturnsModifiedTaxRate() {
        // Given
        BigDecimal fixedCountryRateValue = new BigDecimal("0.1");
        BigDecimal calculatedTaxRateValue = gtRates.taxRate().subtract(gtRates.countryRate()).add(fixedCountryRateValue);
        GtRates expectedGtRate = gtRates
                .withCountryRate(fixedCountryRateValue)
                .withTaxRate(calculatedTaxRateValue);
        JurisdictionalTaxRules givenJurisdictionalSalesTaxRules = jurisdictionalTaxRules
                .withCalculationType(CalculationType.FIXED)
                .withSpecialTreatment(true)
                .withCalculationValue(fixedCountryRateValue);

        // When
        GtRates actualGtRates = countryLevelGtRatesCalculator.calculate(givenJurisdictionalSalesTaxRules, gtRates);

        // Then
        assertEquals(expectedGtRate, actualGtRates);
    }

    @Test
    void getRateByRules_CalculationTypeSetToPercentage_OverridesStateRate() {
        // Given
        JurisdictionalTaxRules percentageCalculationTypeRule = jurisdictionalTaxRules
                .withCalculationType(CalculationType.PERCENTAGE)
                .withSpecialTreatment(true);
        BigDecimal calculatedRate = percentageCalculationTypeRule.getCalculationValue().multiply(gtRates.taxRate());
        GtRates expectedGtRate = gtRates.withTaxRate(calculatedRate);

        // When + Then
        GtRates returnedRate = countryLevelGtRatesCalculator.calculate(percentageCalculationTypeRule, gtRates);
        Assertions.assertEquals(expectedGtRate, returnedRate);
    }

    @Test
    void getRateByRules_NotTaxable_ReturnsZeroRate() {
        // Given
        GtRates zeroGtRates = GtRates.zeroGtRates();
        JurisdictionalTaxRules notTaxableRule = jurisdictionalTaxRules.withTaxable(false);

        // When + Then
        GtRates returnedRate = countryLevelGtRatesCalculator.calculate(notTaxableRule, gtRates);
        Assertions.assertEquals(returnedRate, zeroGtRates);
    }

    @Test
    void calculate_NullJurisdictionalSalesTaxRulesPassed_ThrowsException() {
        // Given
        JurisdictionalTaxRules nullJurisdictionalSalesTaxRules = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> countryLevelGtRatesCalculator.calculate(nullJurisdictionalSalesTaxRules, gtRates));

        assertEquals(nullPointerException.getMessage(), "jurisdictionalTaxRules is marked non-null but is null");
    }

    @Test
    void calculate_NullGtRatessPassed_ThrowsException() {
        // Given
        GtRates nullGtRates = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            countryLevelGtRatesCalculator.calculate(jurisdictionalTaxRules, nullGtRates);
        });

        assertEquals(nullPointerException.getMessage(), "originalGtRate is marked non-null but is null");
    }

}