package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SalesTaxRatesProviderTest {

    private SalesTaxRatesProvider salesTaxRatesProvider;

    private JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;

    private SalesTaxRate salesTaxRate;

    @BeforeEach
    void setup() {
        jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        salesTaxRate = new SalesTaxRate(0.05f,0.05f,0.05f,0.05f,0.05f,0.25f);
        salesTaxRatesProvider = new SalesTaxRatesProvider();
    }

    @Test
    void calculateSalesTaxRate_NotTaxable_ReturnZeroTaxRate() {
        // Given
        SalesTaxRate expectedSalesTaxRate = SalesTaxRate.zeroSalesTaxRate();
        JurisdictionalSalesTaxRules givenJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withTaxable(false);

        // When
        SalesTaxRate actualSalesTaxRate = salesTaxRatesProvider.calculateSalesTaxRate(givenJurisdictionalSalesTaxRules, salesTaxRate);

        // Then
        assertEquals(expectedSalesTaxRate,actualSalesTaxRate);

    }

    @Test
    void calculateSalesTaxRate_NoSpecialTreatment_ReturnSameTaxRate() {
        // Given
        JurisdictionalSalesTaxRules givenJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withSpecialTreatment(false);

        // When
        SalesTaxRate actualSalesTaxRate = salesTaxRatesProvider.calculateSalesTaxRate(givenJurisdictionalSalesTaxRules, salesTaxRate);

        // Then
        assertEquals(salesTaxRate,actualSalesTaxRate);

    }

    @Test
    void calculateSalesTaxRate_FixedCalculation_ReturnModifiedTaxRate() {
        // Given
        SalesTaxRate expectedSalesTaxRate = salesTaxRate.withStateRate(0.1f).withTaxRate(0.3f);
        JurisdictionalSalesTaxRules givenJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules;

        // When
        SalesTaxRate actualSalesTaxRate = salesTaxRatesProvider.calculateSalesTaxRate(givenJurisdictionalSalesTaxRules, salesTaxRate);

        // Then
        assertEquals(expectedSalesTaxRate,actualSalesTaxRate);

    }

    /*@Test
    void calculateSalesTaxRate_percentageCalculation_ReturnModifiedTaxRate() {
        // Given
        SalesTaxRate expectedSalesTaxRate = salesTaxRate.withTaxRate(0.025f);
        JurisdictionalSalesTaxRules givenJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withCalculationType(CalculationType.PERCENTAGE);

        // When
        SalesTaxRate actualSalesTaxRate = salesTaxRatesProvider.calculateSalesTaxRate(givenJurisdictionalSalesTaxRules, salesTaxRate);

        // Then
        assertEquals(expectedSalesTaxRate,actualSalesTaxRate);

    }

     */

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
                CalculationType.FIXED, "description", 0.1f, null);
    }
}