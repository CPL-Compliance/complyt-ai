package com.complyt.domain.sales_tax.product_classification;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class ProductClassificationTest {

    String id;
    private ProductClassification productClassification;
    private UnitTestUtilities testUtilities;

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
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        id = UUID.randomUUID().toString();
        productClassification = createProductClassificationRates();
    }

    private ProductClassification createProductClassificationRates() {
        Map<String, JurisdictionalSalesTaxRules> itemJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", testUtilities.createJurisdictionalSalesTaxRules());
        }};
        Map<String, JurisdictionalTaxRules> itemJurisdictionalTaxRulesMap = new HashMap<>() {{
            put("Armenia", testUtilities.createJurisdictionalTaxRules());
        }};
        return new ProductClassification(id
                , "C1S1", "item", "title", itemJurisdictionalSalesTaxRulesMap, itemJurisdictionalTaxRulesMap, TangibleCategory.TANGIBLE);

    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
                CalculationType.FIXED, "description", new BigDecimal("0.5"), null);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ProductClassification(id=" + productClassification.getId() +
                ", taxCode=" + productClassification.getTaxCode() +
                ", description=" + productClassification.getDescription() +
                ", title=" + productClassification.getTitle() +
                ", jurisdictionalSalesTaxRules=" + productClassification.getJurisdictionalSalesTaxRules() +
                ", jurisdictionalTaxRules=" + productClassification.getJurisdictionalTaxRules() +
                ", tangibleCategory=" + productClassification.getTangibleCategory() + ")";

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