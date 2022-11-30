package com.complyt.domain.sales_tax.product_classification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JurisdictionalSalesTaxRulesTest {

    JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;

    @BeforeEach
    void setUp() {
        jurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules("name","abbreviation",
                true,true,CalculationType.PERCENTAGE,"description",0f,null);
    }

    @Test
    void isCalculatedByPercentage_NotTaxAble_ReturnsFalse() {
        // Given
        JurisdictionalSalesTaxRules notTaxAbleJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withTaxable(false);

        // When
        boolean isCalculatedByPercentage = notTaxAbleJurisdictionalSalesTaxRules.calculatedByPercentageCheck();

        // Then
        assertEquals(false,isCalculatedByPercentage);
    }

    @Test
    void isCalculatedByPercentage_NoSpecialTreatment_ReturnsFalse() {
        // Given
        JurisdictionalSalesTaxRules noSpecialTreatmentJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withSpecialTreatment(false);

        // When
        boolean isCalculatedByPercentage = noSpecialTreatmentJurisdictionalSalesTaxRules.calculatedByPercentageCheck();

        // Then
        assertEquals(false,isCalculatedByPercentage);
    }

    @Test
    void isCalculatedByPercentage_CalculationTypeFixed_ReturnsFalse() {
        // Given
        JurisdictionalSalesTaxRules fixedCalculationTypeJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withCalculationType(CalculationType.FIXED);

        // When
        boolean isCalculatedByPercentage = fixedCalculationTypeJurisdictionalSalesTaxRules.calculatedByPercentageCheck();

        // Then
        assertEquals(false,isCalculatedByPercentage);
    }

    @Test
    void isCalculatedByPercentage_ShouldBeCalculatedByPercentage_ReturnsTrue() {
        // Given

        // When
        boolean isCalculatedByPercentage = jurisdictionalSalesTaxRules.calculatedByPercentageCheck();

        // Then
        assertEquals(true,isCalculatedByPercentage);
    }

    @Test void toString_ReturnString() {
        // Given
        String expectedString = "JurisdictionalSalesTaxRules(name=name, abbreviation=abbreviation, taxable=true, specialTreatment=true, calculationType=PERCENTAGE, description=description, calculationValue=0.0, cities=null)";

        // When
        String actualString = jurisdictionalSalesTaxRules.toString();

        // Then
        assertEquals(expectedString,actualString);
    }

    @Test void Equals_SameJurisdictionalSalesTaxRules_ReturnTrue() {
        // Given
        JurisdictionalSalesTaxRules givenJurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules("name","abbreviation",
                true,true,CalculationType.PERCENTAGE,"description",0f,null);

        // When
        boolean actualBoolean = jurisdictionalSalesTaxRules.equals(givenJurisdictionalSalesTaxRules);

        // Then
        assertTrue(actualBoolean);
    }

}
