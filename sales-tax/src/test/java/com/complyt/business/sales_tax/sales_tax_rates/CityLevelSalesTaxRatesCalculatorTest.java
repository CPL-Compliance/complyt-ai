package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.business.tax.sales_tax.sales_tax_rates.CityLevelSalesTaxRatesCalculator;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.SubJurisdictionalTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CityLevelSalesTaxRatesCalculatorTest {

    CityLevelSalesTaxRatesCalculator cityLevelSalesTaxRatesCalculator;
    UnitTestUtilities testUtilities;
    SubJurisdictionalTaxRules citySalesTaxRules;
    SalesTaxRates salesTaxRates;

    @BeforeEach
    void setUp() {
        cityLevelSalesTaxRatesCalculator = new CityLevelSalesTaxRatesCalculator();
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxRates = testUtilities.createSalesTaxRates();
        citySalesTaxRules = testUtilities.createCitySalesTaxRules();
    }

    @Test
    void calculate_CityRuleIsNotTaxable_ReturnsZeroCityRate() {
        // Given
        SubJurisdictionalTaxRules nonTaxableCityRule = citySalesTaxRules.withTaxable(false);
        SalesTaxRates expectedSalesTaxRate = salesTaxRates.withCityRate(BigDecimal.ZERO);

        // When
        SalesTaxRates actualSalesTaxRate = cityLevelSalesTaxRatesCalculator.calculate(nonTaxableCityRule, salesTaxRates);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_CityRuleIsTaxable_ReturnsTaxableCityRate() {
        // Given
        SubJurisdictionalTaxRules taxableCityRule = citySalesTaxRules.withTaxable(true);

        // When
        SalesTaxRates actualSalesTaxRate = cityLevelSalesTaxRatesCalculator.calculate(taxableCityRule, salesTaxRates);

        // Then
        assertEquals(salesTaxRates, actualSalesTaxRate);
    }

    @Test
    void calculate_CityRuleIsTaxable_ReturnsTaxableCityRateWithRemainingRatesAsZero() {
        // Given
        SubJurisdictionalTaxRules taxableCityRule = citySalesTaxRules.withTaxable(true);
        SalesTaxRates zeroSalesTaxRateWithCityRate = SalesTaxRates.zeroSalesTaxRate().withCityRate(salesTaxRates.cityRate());
        SalesTaxRates expectedSalesTaxRate = zeroSalesTaxRateWithCityRate.withTaxRate(salesTaxRates.cityRate());

        // When
        SalesTaxRates actualSalesTaxRate = cityLevelSalesTaxRatesCalculator.calculate(taxableCityRule, zeroSalesTaxRateWithCityRate);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_CityRuleIsNonTaxable_ReturnsZeroRates() {
        // Given
        SubJurisdictionalTaxRules taxableCityRule = citySalesTaxRules.withTaxable(false);
        SalesTaxRates zeroSalesTaxRateWithCityRate = SalesTaxRates.zeroSalesTaxRate().withCityRate(salesTaxRates.cityRate());
        SalesTaxRates expectedSalesTaxRate = SalesTaxRates.zeroSalesTaxRate();

        // When
        SalesTaxRates actualSalesTaxRate = cityLevelSalesTaxRatesCalculator.calculate(taxableCityRule, zeroSalesTaxRateWithCityRate);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_SpecialTreatmentByFixed_ReturnsFixedCityRate() {
        // Given
        SubJurisdictionalTaxRules taxableCityRule = citySalesTaxRules.withTaxable(true).withSpecialTreatment(true);
        BigDecimal newCityRate = taxableCityRule.getCalculationValue();
        SalesTaxRates salesTaxRates = UnitTestUtilities.createCaliforniaSalesTaxRates()
                .withCityRate(newCityRate);
        BigDecimal taxRate = newCityRate.add(salesTaxRates.combinedDistrictRate())
                .add(salesTaxRates.countyRate()).add(salesTaxRates.stateRate());

        SalesTaxRates expectedSalesTaxRate = salesTaxRates.withTaxRate(taxRate).withCityRate(newCityRate);

        // When
        SalesTaxRates actualSalesTaxRate = cityLevelSalesTaxRatesCalculator.calculate(taxableCityRule, salesTaxRates);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_SpecialTreatmentByPercentage_ReturnsPercentageCityRate() {
        // Given
        SubJurisdictionalTaxRules taxableCityRule = citySalesTaxRules.withTaxable(true)
                .withSpecialTreatment(true)
                .withCalculationType(CalculationType.PERCENTAGE);
        SalesTaxRates originalSalesTaxRate = UnitTestUtilities.createCaliforniaSalesTaxRates().withCityRate(new BigDecimal("0.05"));
        BigDecimal newCityTaxRate = originalSalesTaxRate.cityRate().multiply(taxableCityRule.getCalculationValue());
        BigDecimal taxRate = newCityTaxRate.add(originalSalesTaxRate.combinedDistrictRate())
                .add(originalSalesTaxRate.countyRate()).add(originalSalesTaxRate.stateRate());

        SalesTaxRates expectedSalesTaxRate = originalSalesTaxRate.withTaxRate(taxRate).withCityRate(newCityTaxRate);

        // When
        SalesTaxRates actualSalesTaxRate = cityLevelSalesTaxRatesCalculator.calculate(taxableCityRule, originalSalesTaxRate);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_NullCitySalesTaxRulesPassed_ThrowsException() {
        // Given
        SubJurisdictionalTaxRules nullCitySalesTaxRules = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            cityLevelSalesTaxRatesCalculator.calculate(nullCitySalesTaxRules, salesTaxRates);
        });

        assertEquals(nullPointerException.getMessage(), "citySalesTaxRules is marked non-null but is null");
    }

    @Test
    void calculate_NullSalesTaxRatePassed_ThrowsException() {
        // Given
        SalesTaxRates nullSalesTaxRate = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            cityLevelSalesTaxRatesCalculator.calculate(citySalesTaxRules, nullSalesTaxRate);
        });

        assertEquals(nullPointerException.getMessage(), "originalSalesTaxRate is marked non-null but is null");
    }

}
