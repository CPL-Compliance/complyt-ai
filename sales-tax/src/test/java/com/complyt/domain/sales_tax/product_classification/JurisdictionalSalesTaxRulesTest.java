package com.complyt.domain.sales_tax.product_classification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JurisdictionalSalesTaxRulesTest {

    JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;

    @BeforeEach
    void setUp() {
        jurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules("name", "abbreviation",
                true, true, CalculationType.PERCENTAGE, "description", BigDecimal.ZERO, null);
    }

    @Test
    void isCalculatedByPercentage_NotTaxAble_ReturnsFalse() {
        // Given
        JurisdictionalSalesTaxRules notTaxAbleJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withTaxable(false);

        // When
        boolean isCalculatedByPercentage = notTaxAbleJurisdictionalSalesTaxRules.calculatedByPercentageCheck();

        // Then
        assertFalse(isCalculatedByPercentage);
    }

    @Test
    void isCalculatedByPercentage_NoSpecialTreatment_ReturnsFalse() {
        // Given
        JurisdictionalSalesTaxRules noSpecialTreatmentJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withSpecialTreatment(false);

        // When
        boolean isCalculatedByPercentage = noSpecialTreatmentJurisdictionalSalesTaxRules.calculatedByPercentageCheck();

        // Then
        assertFalse(isCalculatedByPercentage);
    }

    @Test
    void isCalculatedByPercentage_CalculationTypeFixed_ReturnsFalse() {
        // Given
        JurisdictionalSalesTaxRules fixedCalculationTypeJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withCalculationType(CalculationType.FIXED);

        // When
        boolean isCalculatedByPercentage = fixedCalculationTypeJurisdictionalSalesTaxRules.calculatedByPercentageCheck();

        // Then
        assertFalse(isCalculatedByPercentage);
    }

    @Test
    void isCalculatedByPercentage_ShouldBeCalculatedByPercentage_ReturnsTrue() {
        // Given

        // When
        boolean isCalculatedByPercentage = jurisdictionalSalesTaxRules.calculatedByPercentageCheck();

        // Then
        assertTrue(isCalculatedByPercentage);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "JurisdictionalSalesTaxRules(name=" + jurisdictionalSalesTaxRules.getName() +
                ", abbreviation=" + jurisdictionalSalesTaxRules.getAbbreviation() +
                ", taxable=" + jurisdictionalSalesTaxRules.isTaxable() +
                ", specialTreatment=" + jurisdictionalSalesTaxRules.isSpecialTreatment() +
                ", calculationType=" + jurisdictionalSalesTaxRules.getCalculationType() +
                ", description=" + jurisdictionalSalesTaxRules.getDescription() +
                ", calculationValue=" + jurisdictionalSalesTaxRules.getCalculationValue() +
                ", cities=" + jurisdictionalSalesTaxRules.getCities() + ")";

        // When
        String actualString = jurisdictionalSalesTaxRules.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameJurisdictionalSalesTaxRules_ReturnTrue() {
        // Given
        JurisdictionalSalesTaxRules givenJurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules("name", "abbreviation",
                true, true, CalculationType.PERCENTAGE, "description", BigDecimal.ZERO, null);

        // When
        boolean isEquals = jurisdictionalSalesTaxRules.equals(givenJurisdictionalSalesTaxRules);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void getCalculationValue_CalculationValueIsNull_ReturnsZero() {
        // Given
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRulesWithNullManualTaxRate = jurisdictionalSalesTaxRules.withCalculationValue(null);

        // When
        BigDecimal actualTotalPrice = jurisdictionalSalesTaxRulesWithNullManualTaxRate.getCalculationValue();

        // Then
        assertEquals(BigDecimal.ZERO, actualTotalPrice);
    }

    @Test
    void getCalculationValue_CalculationValueIs10_ReturnsBigDecimalOf10() {
        // Given
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRulesWithManualTaxRateOf10 = jurisdictionalSalesTaxRules.withCalculationValue(new BigDecimal("10"));

        // When
        BigDecimal actualTotalPrice = jurisdictionalSalesTaxRulesWithManualTaxRateOf10.getCalculationValue();

        // Then
        assertEquals(new BigDecimal("10"), actualTotalPrice);
    }

}
