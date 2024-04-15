package com.complyt.domain.sales_tax.product_classification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CitySalesTaxRulesTest {

    private SubJurisdictionalTaxRules SubJurisdictionalTaxRules;

    @BeforeEach
    void setup() {
        SubJurisdictionalTaxRules = createCitySalesTaxRulesRates();
    }

    private SubJurisdictionalTaxRules createCitySalesTaxRulesRates() {
        return new SubJurisdictionalTaxRules("California", "Ca", false, false, CalculationType.FIXED, "", new BigDecimal("1000"));
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "SubJurisdictionalTaxRules(name=" + SubJurisdictionalTaxRules.getName() +
                ", abbreviation=" + SubJurisdictionalTaxRules.getAbbreviation() +
                ", taxable=" + SubJurisdictionalTaxRules.isTaxable() +
                ", specialTreatment=" + SubJurisdictionalTaxRules.isSpecialTreatment() +
                ", calculationType=" + SubJurisdictionalTaxRules.getCalculationType() +
                ", description=" + SubJurisdictionalTaxRules.getDescription() +
                ", calculationValue=" + SubJurisdictionalTaxRules.getCalculationValue() + ")";

        // When
        String actualString = SubJurisdictionalTaxRules.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameCitySalesTaxRules_ReturnTrue() {
        // Given
        SubJurisdictionalTaxRules givenCitySalesTaxRules = createCitySalesTaxRulesRates();

        // When
        boolean isEquals = SubJurisdictionalTaxRules.equals(givenCitySalesTaxRules);

        // Then
        assertTrue(isEquals);
    }

}
