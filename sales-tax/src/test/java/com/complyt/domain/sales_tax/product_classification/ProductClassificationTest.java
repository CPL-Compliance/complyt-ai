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

    String id;
    private ProductClassification productClassification;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID().toString();
        productClassification = createProductClassificationRates();
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

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ProductClassification(id=" + productClassification.getId() +
                ", taxCode=" + productClassification.getTaxCode() +
                ", description=item, title=title, jurisdictionalSalesTaxRules=" + productClassification.getJurisdictionalSalesTaxRules() +
                ", tangibleCategory=TANGIBLE)";

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
        boolean isEquals = productClassification.equals(givenProductClassification);

        // Then
        assertTrue(isEquals);
    }

}