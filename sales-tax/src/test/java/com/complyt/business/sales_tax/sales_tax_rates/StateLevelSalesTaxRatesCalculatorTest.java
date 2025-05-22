package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.business.tax.sales_tax.sales_tax_rates.StateLevelSalesTaxRatesCalculator;
import com.complyt.domain.sales_tax.RatesMetaData;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

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
        BigDecimal fixedStateRateValue = new BigDecimal("0.1");

        SalesTaxRates expectedSalesTaxRate = salesTaxRates
                .withStateRate(fixedStateRateValue)
                .withCountyRate(BigDecimal.ZERO)
                .withCityRate(BigDecimal.ZERO)
                .withMtaRate(BigDecimal.ZERO)
                .withSpdRate(BigDecimal.ZERO)
                .withOtherRate(BigDecimal.ZERO)
                .withRatesMetaData(new RatesMetaData(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
                .withTaxRate(fixedStateRateValue);

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
                .withCalculationType(CalculationType.PERCENTAGE) // value=0.05
                .withSpecialTreatment(true);

        BigDecimal calculatedTaxRate = percentageCalculationTypeRule.getCalculationValue().multiply(salesTaxRates.taxRate()).stripTrailingZeros();
        BigDecimal calculatedRate = percentageCalculationTypeRule.getCalculationValue().multiply(salesTaxRates.stateRate()).stripTrailingZeros();
        SalesTaxRates expectedSalesTaxRate = salesTaxRates.withStateRate(calculatedRate).withCityRate(calculatedRate).withCountyRate(calculatedRate).withTaxRate(calculatedTaxRate);

        // When + Then
        SalesTaxRates returnedRate = stateLevelSalesTaxRatesCalculator.calculate(percentageCalculationTypeRule, salesTaxRates);
        Assertions.assertEquals(expectedSalesTaxRate, returnedRate);
    }

    @Test
    void getRateByRules_CalculationTypeSetToPercentage_RatesMetaDataNotNull_OverridesStateRate() {
        // Given
        RatesMetaData ratesMetaDataOriginal = new RatesMetaData(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO);
        RatesMetaData ratesMetaDataPercentage = ratesMetaDataOriginal.withCityDistrictRate(BigDecimal.valueOf(0.5));
        salesTaxRates = salesTaxRates.withRatesMetaData(ratesMetaDataOriginal);
        JurisdictionalSalesTaxRules percentageCalculationTypeRule = jurisdictionalSalesTaxRules
                .withCalculationType(CalculationType.PERCENTAGE) // value=0.05
                .withSpecialTreatment(true);

        BigDecimal calculatedTaxRate = percentageCalculationTypeRule.getCalculationValue().multiply(salesTaxRates.taxRate()).stripTrailingZeros();
        BigDecimal calculatedRate = percentageCalculationTypeRule.getCalculationValue().multiply(salesTaxRates.stateRate()).stripTrailingZeros();
        SalesTaxRates expectedSalesTaxRate = salesTaxRates.withStateRate(calculatedRate).withCityRate(calculatedRate).withCountyRate(calculatedRate).withRatesMetaData(ratesMetaDataPercentage).withTaxRate(calculatedTaxRate);

        // When + Then
        SalesTaxRates returnedRate = stateLevelSalesTaxRatesCalculator.calculate(percentageCalculationTypeRule, salesTaxRates);
        Assertions.assertEquals(expectedSalesTaxRate, returnedRate);
    }

    @Test
    void getRateByRules_NotTaxable_ReturnsZeroRate() {
        // Given
        SalesTaxRates zeroSalesTaxRate = new SalesTaxRates(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new RatesMetaData(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
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
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> stateLevelSalesTaxRatesCalculator.calculate(nullJurisdictionalSalesTaxRules, salesTaxRates));

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
