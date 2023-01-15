package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SalesTaxRatesProviderTest {

    private SalesTaxRatesProvider salesTaxRatesProvider;

    private JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;

    private SalesTaxRate salesTaxRate;

    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setup() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        jurisdictionalSalesTaxRules = domainObjectStub.createJurisdictionalSalesTaxRules();
        salesTaxRate = domainObjectStub.createSalesTaxRates();
        salesTaxRatesProvider = new SalesTaxRatesProvider();
    }

    @Test
    void calculateSalesTaxRate_NotTaxable_ReturnsZeroTaxRate() {
        // Given
        SalesTaxRate expectedSalesTaxRate = SalesTaxRate.zeroSalesTaxRate();
        JurisdictionalSalesTaxRules givenJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withTaxable(false);

        // When
        SalesTaxRate actualSalesTaxRate = salesTaxRatesProvider.calculateSalesTaxRate(givenJurisdictionalSalesTaxRules, salesTaxRate);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculateSalesTaxRate_NoSpecialTreatment_ReturnsSameTaxRate() {
        // Given
        JurisdictionalSalesTaxRules givenJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withSpecialTreatment(false);

        // When
        SalesTaxRate actualSalesTaxRate = salesTaxRatesProvider.calculateSalesTaxRate(givenJurisdictionalSalesTaxRules, salesTaxRate);

        // Then
        assertEquals(salesTaxRate, actualSalesTaxRate);
    }

    @Test
    void calculateSalesTaxRate_FixedCalculation_ReturnsModifiedTaxRate() {
        // Given
        float fixedStateRateValue = 0.1f;
        float calculatedTaxRateValue = salesTaxRate.getTaxRate() - salesTaxRate.getStateRate() + fixedStateRateValue;
        SalesTaxRate expectedSalesTaxRate = salesTaxRate
                .withStateRate(fixedStateRateValue)
                .withTaxRate(calculatedTaxRateValue);
        JurisdictionalSalesTaxRules givenJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules
                .withCalculationType(CalculationType.FIXED)
                .withCalculationValue(fixedStateRateValue);

        // When
        SalesTaxRate actualSalesTaxRate = salesTaxRatesProvider.calculateSalesTaxRate(givenJurisdictionalSalesTaxRules, salesTaxRate);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void getRateByRules_CalculationTypeSetToPercentage_OverridesStateRate() {
        // Given
        JurisdictionalSalesTaxRules percentageCalculationTypeRule = jurisdictionalSalesTaxRules
                .withCalculationType(CalculationType.PERCENTAGE)
                .withSpecialTreatment(true);
        float calculatedRate = percentageCalculationTypeRule.getCalculationValue() * salesTaxRate.getTaxRate();
        SalesTaxRate expectedSalesTaxRate = salesTaxRate.withTaxRate(calculatedRate);

        // When + Then
        SalesTaxRate returnedRate = salesTaxRatesProvider.calculateSalesTaxRate(percentageCalculationTypeRule, salesTaxRate);
        Assertions.assertEquals(expectedSalesTaxRate, returnedRate);
    }

    @Test
    void getRateByRules_NotTaxable_ReturnsZeroRate() {
        // Given
        SalesTaxRate zeroSalesTaxRate = new SalesTaxRate(0, 0, 0, 0, 0, 0);
        JurisdictionalSalesTaxRules notTaxableRule = jurisdictionalSalesTaxRules.withTaxable(false);

        // When + Then
        SalesTaxRate returnedRate = salesTaxRatesProvider.calculateSalesTaxRate(notTaxableRule, salesTaxRate);
        Assertions.assertEquals(returnedRate, zeroSalesTaxRate);
    }

    @Test
    void calculateSalesTaxRate_NullJurisdictionalSalesTaxRulesPassed_ThrowsException() {
        // Given
        JurisdictionalSalesTaxRules nullJurisdictionalSalesTaxRules = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxRatesProvider.calculateSalesTaxRate(nullJurisdictionalSalesTaxRules, salesTaxRate);
        });

        assertEquals(nullPointerException.getMessage(), "jurisdictionalSalesTaxRules is marked non-null but is null");
    }

    @Test
    void calculateSalesTaxRate_NullSalesTaxRatesPassed_ThrowsException() {
        // Given
        SalesTaxRate nullSalesTaxRate = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxRatesProvider.calculateSalesTaxRate(jurisdictionalSalesTaxRules, nullSalesTaxRate);
        });

        assertEquals(nullPointerException.getMessage(), "originalSalesTaxRate is marked non-null but is null");
    }

}