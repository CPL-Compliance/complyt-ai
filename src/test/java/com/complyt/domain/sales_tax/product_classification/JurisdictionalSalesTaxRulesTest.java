package com.complyt.domain.sales_tax.product_classification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
        boolean isCalculatedByPercentage = notTaxAbleJurisdictionalSalesTaxRules.isCalculatedByPercentage();

        // Then
        Assertions.assertEquals(false,isCalculatedByPercentage);
    }

    @Test
    void isCalculatedByPercentage_NoSpecialTreatment_ReturnsFalse() {
        // Given
        JurisdictionalSalesTaxRules noSpecialTreatmentJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withSpecialTreatment(false);

        // When
        boolean isCalculatedByPercentage = noSpecialTreatmentJurisdictionalSalesTaxRules.isCalculatedByPercentage();

        // Then
        Assertions.assertEquals(false,isCalculatedByPercentage);
    }

    @Test
    void isCalculatedByPercentage_CalculationTypeFixed_ReturnsFalse() {
        // Given
        JurisdictionalSalesTaxRules fixedCalculationTypeJurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules.withCalculationType(CalculationType.FIXED);

        // When
        boolean isCalculatedByPercentage = fixedCalculationTypeJurisdictionalSalesTaxRules.isCalculatedByPercentage();

        // Then
        Assertions.assertEquals(false,isCalculatedByPercentage);
    }

    @Test
    void isCalculatedByPercentage_ShouldBeCalculatedByPercentage_ReturnsTrue() {
        // Given

        // When
        boolean isCalculatedByPercentage = jurisdictionalSalesTaxRules.isCalculatedByPercentage();

        // Then
        Assertions.assertEquals(true,isCalculatedByPercentage);
    }

}
