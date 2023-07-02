package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.CitySalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CityLevelSalesTaxRatesCalculatorTest {

    CityLevelSalesTaxRatesCalculator cityLevelSalesTaxRatesCalculator;
    UnitTestUtilities testUtilities;
    CitySalesTaxRules citySalesTaxRules;
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
        CitySalesTaxRules nonTaxableCityRule = citySalesTaxRules.withTaxable(false);
        SalesTaxRates expectedSalesTaxRate = salesTaxRates.withCityRate(0);

        // When
        SalesTaxRates actualSalesTaxRate = cityLevelSalesTaxRatesCalculator.calculate(nonTaxableCityRule, salesTaxRates);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_CityRuleIsTaxable_ReturnsTaxableCityRate() {
        // Given
        CitySalesTaxRules taxableCityRule = citySalesTaxRules.withTaxable(true);

        // When
        SalesTaxRates actualSalesTaxRate = cityLevelSalesTaxRatesCalculator.calculate(taxableCityRule, salesTaxRates);

        // Then
        assertEquals(salesTaxRates, actualSalesTaxRate);
    }

    @Test
    void calculate_CityRuleIsTaxable_ReturnsTaxableCityRateWithRemainingRatesAsZero() {
        // Given
        CitySalesTaxRules taxableCityRule = citySalesTaxRules.withTaxable(true);
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
        CitySalesTaxRules taxableCityRule = citySalesTaxRules.withTaxable(false);
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
        CitySalesTaxRules taxableCityRule = citySalesTaxRules.withTaxable(true).withSpecialTreatment(true);
        float newCityRate = taxableCityRule.getCalculationValue();
        SalesTaxRates salesTaxRates = UnitTestUtilities.createCaliforniaSalesTaxRates()
                .withCityRate(newCityRate);
        float taxRate = newCityRate + salesTaxRates.cityDistrictRate() + salesTaxRates.countyDistrictRate() +
                salesTaxRates.countyRate() + salesTaxRates.stateRate();

        SalesTaxRates expectedSalesTaxRate = salesTaxRates.withTaxRate(taxRate).withCityRate(newCityRate);

        // When
        SalesTaxRates actualSalesTaxRate = cityLevelSalesTaxRatesCalculator.calculate(taxableCityRule, salesTaxRates);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_SpecialTreatmentByPercentage_ReturnsPercentageCityRate() {
        // Given
        CitySalesTaxRules taxableCityRule = citySalesTaxRules.withTaxable(true)
                .withSpecialTreatment(true)
                .withCalculationType(CalculationType.PERCENTAGE);
        SalesTaxRates originalSalesTaxRate = UnitTestUtilities.createCaliforniaSalesTaxRates().withCityRate(0.05f);
        float newCityTaxRate = originalSalesTaxRate.cityRate() * taxableCityRule.getCalculationValue();
        float taxRate = newCityTaxRate + originalSalesTaxRate.cityDistrictRate() + originalSalesTaxRate.countyDistrictRate() +
                originalSalesTaxRate.countyRate() + originalSalesTaxRate.stateRate();

        SalesTaxRates expectedSalesTaxRate = originalSalesTaxRate.withTaxRate(taxRate).withCityRate(newCityTaxRate);

        // When
        SalesTaxRates actualSalesTaxRate = cityLevelSalesTaxRatesCalculator.calculate(taxableCityRule, originalSalesTaxRate);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_NullCitySalesTaxRulesPassed_ThrowsException() {
        // Given
        CitySalesTaxRules nullCitySalesTaxRules = null;

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
