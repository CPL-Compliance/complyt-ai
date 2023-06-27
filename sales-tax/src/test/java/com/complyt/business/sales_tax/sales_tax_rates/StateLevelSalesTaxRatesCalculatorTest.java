package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StateLevelSalesTaxRatesCalculatorTest {

    StateLevelSalesTaxRatesCalculator stateLevelSalesTaxRatesCalculator;
    UnitTestUtilities testUtilities;
    JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    SalesTaxRates salesTaxRates;

    @BeforeEach
    void setUp() {
        stateLevelSalesTaxRatesCalculator = new StateLevelSalesTaxRatesCalculator();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxRates = testUtilities.createSalesTaxRates();
        jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules();
    }

    @Test
    void calculateSalesTaxRate_StateLevelNotTaxable_ReturnsZeroTaxRate() {
        // Given
        SalesTaxRates expectedSalesTaxRate = SalesTaxRates.zeroSalesTaxRate();
        JurisdictionalSalesTaxRules givenJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withTaxable(false);

        // When
        SalesTaxRates actualSalesTaxRate = stateLevelSalesTaxRatesCalculator.calculate(givenJurisdictionalSalesTaxRules, salesTaxRates);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculateSalesTaxRate_StateLevelHasNoSpecialTreatment_ReturnsSameTaxRate() {
        // Given
        JurisdictionalSalesTaxRules givenJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withSpecialTreatment(false);

        // When
        SalesTaxRates actualSalesTaxRate = stateLevelSalesTaxRatesCalculator.calculate(givenJurisdictionalSalesTaxRules, salesTaxRates);

        // Then
        assertEquals(salesTaxRates, actualSalesTaxRate);
    }

    @Test
    void calculateSalesTaxRate_FixedCalculation_ReturnsModifiedTaxRate() {
        // Given
        float fixedStateRateValue = 0.1f;
        float calculatedTaxRateValue = salesTaxRates.taxRate() - salesTaxRates.stateRate() + fixedStateRateValue;
        SalesTaxRates expectedSalesTaxRate = salesTaxRates
                .withStateRate(fixedStateRateValue)
                .withTaxRate(calculatedTaxRateValue);
        JurisdictionalSalesTaxRules givenJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules
                .withCalculationType(CalculationType.FIXED)
                .withSpecialTreatment(true)
                .withCalculationValue(fixedStateRateValue);

        // When
        SalesTaxRates actualSalesTaxRate = stateLevelSalesTaxRatesCalculator.calculate(givenJurisdictionalSalesTaxRules, salesTaxRates);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void getRateByRules_CalculationTypeSetToPercentage_OverridesStateRate() {
        // Given
        JurisdictionalSalesTaxRules percentageCalculationTypeRule = jurisdictionalSalesTaxRules
                .withCalculationType(CalculationType.PERCENTAGE)
                .withSpecialTreatment(true);
        float calculatedRate = percentageCalculationTypeRule.getCalculationValue() * salesTaxRates.taxRate();
        SalesTaxRates expectedSalesTaxRate = salesTaxRates.withTaxRate(calculatedRate);

        // When + Then
        SalesTaxRates returnedRate = stateLevelSalesTaxRatesCalculator.calculate(percentageCalculationTypeRule, salesTaxRates);
        Assertions.assertEquals(expectedSalesTaxRate, returnedRate);
    }

    @Test
    void getRateByRules_NotTaxable_ReturnsZeroRate() {
        // Given
        SalesTaxRates zeroSalesTaxRate = new SalesTaxRates(0, 0, 0, 0, 0, null);
        JurisdictionalSalesTaxRules notTaxableRule = jurisdictionalSalesTaxRules.withTaxable(false);

        // When + Then
        SalesTaxRates returnedRate = stateLevelSalesTaxRatesCalculator.calculate(notTaxableRule, salesTaxRates);
        Assertions.assertEquals(returnedRate, zeroSalesTaxRate);
    }

    @Test
    void calculate_NullJurisdictionalSalesTaxRulesPassed_ThrowsException() {
        // Given
        JurisdictionalSalesTaxRules nullJurisdictionalSalesTaxRules = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            stateLevelSalesTaxRatesCalculator.calculate(nullJurisdictionalSalesTaxRules, salesTaxRates);
        });

        assertEquals(nullPointerException.getMessage(), "jurisdictionalSalesTaxRules is marked non-null but is null");
    }

    @Test
    void calculate_NullSalesTaxRatesPassed_ThrowsException() {
        // Given
        SalesTaxRates nullSalesTaxRate = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            stateLevelSalesTaxRatesCalculator.calculate(jurisdictionalSalesTaxRules, nullSalesTaxRate);
        });

        assertEquals(nullPointerException.getMessage(), "originalSalesTaxRate is marked non-null but is null");
    }
}
