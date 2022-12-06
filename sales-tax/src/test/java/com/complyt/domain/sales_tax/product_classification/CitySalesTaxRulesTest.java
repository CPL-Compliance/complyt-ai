package com.complyt.domain.sales_tax.product_classification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CitySalesTaxRulesTest {

    private CitySalesTaxRules citySalesTaxRules;

    @BeforeEach
    void setup() {
        citySalesTaxRules = createCitySalesTaxRulesRates();
    }

    private CitySalesTaxRules createCitySalesTaxRulesRates() {
        return new CitySalesTaxRules("California", "Ca", false, false, CalculationType.FIXED, "", 1000f);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "CitySalesTaxRules(name=California, abbreviation=Ca, taxable=false, specialTreatment=false, calculationType=FIXED, description=, calculationValue=1000.0)";

        // When
        String actualString = citySalesTaxRules.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameCitySalesTaxRules_ReturnTrue() {
        // Given
        CitySalesTaxRules givenCitySalesTaxRules = createCitySalesTaxRulesRates();

        // When
        boolean isEquals = citySalesTaxRules.equals(givenCitySalesTaxRules);

        // Then
        assertTrue(isEquals);
    }

}
