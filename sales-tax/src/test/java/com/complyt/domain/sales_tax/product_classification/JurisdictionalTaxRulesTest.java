package com.complyt.domain.sales_tax.product_classification;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class JurisdictionalTaxRulesTest {
    JurisdictionalTaxRules jurisdictionalTaxRules;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        jurisdictionalTaxRules = new JurisdictionalTaxRules("name", "abbreviation",
                true, true, CalculationType.PERCENTAGE, "description", BigDecimal.ZERO, null);
    }

    @Test
    void isCalculatedByPercentage_NotTaxAble_ReturnsFalse() {
        // Given
        JurisdictionalTaxRules notTaxAbleJurisdictionalTaxRules = jurisdictionalTaxRules.withTaxable(false);

        // When
        boolean isCalculatedByPercentage = notTaxAbleJurisdictionalTaxRules.calculatedByPercentageCheck();

        // Then
        assertFalse(isCalculatedByPercentage);
    }

    @Test
    void isCalculatedByPercentage_NoSpecialTreatment_ReturnsFalse() {
        // Given
        JurisdictionalTaxRules noSpecialTreatmentJurisdictionalTaxRules = jurisdictionalTaxRules.withSpecialTreatment(false);

        // When
        boolean isCalculatedByPercentage = noSpecialTreatmentJurisdictionalTaxRules.calculatedByPercentageCheck();

        // Then
        assertFalse(isCalculatedByPercentage);
    }

    @Test
    void isCalculatedByPercentage_CalculationTypeFixed_ReturnsFalse() {
        // Given
        JurisdictionalTaxRules fixedCalculationTypeJurisdictionalTaxRules = jurisdictionalTaxRules.withCalculationType(CalculationType.FIXED);

        // When
        boolean isCalculatedByPercentage = fixedCalculationTypeJurisdictionalTaxRules.calculatedByPercentageCheck();

        // Then
        assertFalse(isCalculatedByPercentage);
    }

    @Test
    void isCalculatedByPercentage_ShouldBeCalculatedByPercentage_ReturnsTrue() {
        // Given

        // When
        boolean isCalculatedByPercentage = jurisdictionalTaxRules.calculatedByPercentageCheck();

        // Then
        assertTrue(isCalculatedByPercentage);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "JurisdictionalTaxRules(name=" + jurisdictionalTaxRules.getName() +
                ", abbreviation=" + jurisdictionalTaxRules.getAbbreviation() +
                ", taxable=" + jurisdictionalTaxRules.isTaxable() +
                ", specialTreatment=" + jurisdictionalTaxRules.isSpecialTreatment() +
                ", calculationType=" + jurisdictionalTaxRules.getCalculationType() +
                ", description=" + jurisdictionalTaxRules.getDescription() +
                ", calculationValue=" + jurisdictionalTaxRules.getCalculationValue() +
                ", regions=" + jurisdictionalTaxRules.getRegions() +
                ")";

        // When
        String actualString = jurisdictionalTaxRules.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameJurisdictionalTaxRules_ReturnTrue() {
        // Given
        JurisdictionalTaxRules givenJurisdictionalTaxRules = new JurisdictionalTaxRules("name", "abbreviation",
                true, true, CalculationType.PERCENTAGE, "description", BigDecimal.ZERO, null);

        // When
        boolean isEquals = jurisdictionalTaxRules.equals(givenJurisdictionalTaxRules);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void getCalculationValue_CalculationValueIsNull_ReturnsZero() {
        // Given
        JurisdictionalTaxRules jurisdictionalTaxRulesWithNullManualTaxRate = jurisdictionalTaxRules.withCalculationValue(null);

        // When
        BigDecimal actualTotalPrice = jurisdictionalTaxRulesWithNullManualTaxRate.getCalculationValue();

        // Then
        assertEquals(BigDecimal.ZERO, actualTotalPrice);
    }

    @Test
    void getCalculationValue_CalculationValueIs10_ReturnsBigDecimalOf10() {
        // Given
        JurisdictionalTaxRules jurisdictionalTaxRulesWithManualTaxRateOf10 = jurisdictionalTaxRules.withCalculationValue(new BigDecimal("10"));

        // When
        BigDecimal actualTotalPrice = jurisdictionalTaxRulesWithManualTaxRateOf10.getCalculationValue();

        // Then
        assertEquals(new BigDecimal("10"), actualTotalPrice);
    }

}
