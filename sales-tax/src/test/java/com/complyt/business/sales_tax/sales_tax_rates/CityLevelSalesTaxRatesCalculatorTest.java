package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CitySalesTaxRules;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CityLevelSalesTaxRatesCalculatorTest {

    CityLevelSalesTaxRatesCalculator cityLevelSalesTaxRatesCalculator;
    ObjectStub objectStub;
    CitySalesTaxRules citySalesTaxRules;
    SalesTaxRate salesTaxRate;

    @BeforeEach
    void setUp() {
        cityLevelSalesTaxRatesCalculator = new CityLevelSalesTaxRatesCalculator();
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        salesTaxRate = objectStub.createSalesTaxRates();
        citySalesTaxRules = objectStub.createCitySalesTaxRules();
    }

    @Test
    void calculate_CityRuleIsNotTaxable_ReturnsZeroCityRate() {
        // Given
        CitySalesTaxRules nonTaxableCityRule = citySalesTaxRules.withTaxable(false);
        SalesTaxRate expectedSalesTaxRate = salesTaxRate.withCityRate(0);

        // When
        SalesTaxRate actualSalesTaxRate = cityLevelSalesTaxRatesCalculator.calculate(nonTaxableCityRule, salesTaxRate);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_CityRuleIsTaxable_ReturnsTaxableCityRate() {
        // Given
        CitySalesTaxRules taxableCityRule = citySalesTaxRules.withTaxable(true);

        // When
        SalesTaxRate actualSalesTaxRate = cityLevelSalesTaxRatesCalculator.calculate(taxableCityRule, salesTaxRate);

        // Then
        assertEquals(salesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_CityRuleIsTaxable_ReturnsTaxableCityRateWithRemainingRatesAsZero() {
        // Given
        CitySalesTaxRules taxableCityRule = citySalesTaxRules.withTaxable(true);
        SalesTaxRate zeroSalesTaxRateWithCityRate = SalesTaxRate.zeroSalesTaxRate().withCityRate(salesTaxRate.getCityRate());
        SalesTaxRate expectedSalesTaxRate = zeroSalesTaxRateWithCityRate.withTaxRate(salesTaxRate.getCityRate());

        // When
        SalesTaxRate actualSalesTaxRate = cityLevelSalesTaxRatesCalculator.calculate(taxableCityRule, zeroSalesTaxRateWithCityRate);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_CityRuleIsNonTaxable_ReturnsZeroRates() {
        // Given
        CitySalesTaxRules taxableCityRule = citySalesTaxRules.withTaxable(false);
        SalesTaxRate zeroSalesTaxRateWithCityRate = SalesTaxRate.zeroSalesTaxRate().withCityRate(salesTaxRate.getCityRate());
        SalesTaxRate expectedSalesTaxRate = SalesTaxRate.zeroSalesTaxRate();

        // When
        SalesTaxRate actualSalesTaxRate = cityLevelSalesTaxRatesCalculator.calculate(taxableCityRule, zeroSalesTaxRateWithCityRate);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculate_NullCitySalesTaxRulesPassed_ThrowsException() {
        // Given
        CitySalesTaxRules nullCitySalesTaxRules = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            cityLevelSalesTaxRatesCalculator.calculate(nullCitySalesTaxRules, salesTaxRate);
        });

        assertEquals(nullPointerException.getMessage(), "citySalesTaxRules is marked non-null but is null");
    }

    @Test
    void calculate_NullSalesTaxRatePassed_ThrowsException() {
        // Given
        SalesTaxRate nullSalesTaxRate = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            cityLevelSalesTaxRatesCalculator.calculate(citySalesTaxRules, nullSalesTaxRate);
        });

        assertEquals(nullPointerException.getMessage(), "originalSalesTaxRate is marked non-null but is null");
    }

}
