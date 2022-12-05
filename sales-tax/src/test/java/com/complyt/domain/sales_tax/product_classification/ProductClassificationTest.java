package com.complyt.domain.sales_tax.product_classification;

import com.complyt.domain.nexus.enums.TangibleCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductClassificationTest {

    private ProductClassification productClassification;
    String id;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID().toString();
        productClassification = createProductClassificationRates();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ProductClassification(id=" + id + ", taxCode=C1S1, description=item, title=title, jurisdictionalSalesTaxRules={CA=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=true, calculationType=FIXED, description=description, calculationValue=0.5, cities=null)}, tangibleCategory=TANGIBLE)";

        // When
        String actualString = productClassification.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameProductClassification_ReturnTrue() {
        // Given
        ProductClassification givenProductClassification = createProductClassificationRates();

        // When
        boolean actualBoolean = productClassification.equals(givenProductClassification);

        // Then
        assertTrue(actualBoolean);
    }

    private ProductClassification createProductClassificationRates() {
        Map<String, JurisdictionalSalesTaxRules> itemJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", createJurisdictionalSalesTaxRules());
        }};
        return new ProductClassification(id
                , "C1S1", "item", "title", itemJurisdictionalSalesTaxRulesMap, TangibleCategory.TANGIBLE);

    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
                CalculationType.FIXED, "description", 0.5f, null);
    }
}