package com.complyt.business.strategy;

import com.complyt.business.strategy.items_jurisdictional_rules_injection.NonUsaAddressItemsJurisdictionalRulesInjector;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import com.complyt.domain.sales_tax.product_classification.SubJurisdictionalTaxRules;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mockStatic;

public class NonUsaAddressRegionExtractorTest {

    NonUsaAddressItemsJurisdictionalRulesInjector nonUsaAddressItemsJurisdictionalRulesInjector;
    UnitTestUtilities testUtilities;

    JurisdictionalTaxRules jurisdictionalTaxRules;
    SubJurisdictionalTaxRules firstSubJurisdictionalTaxRules;
    SubJurisdictionalTaxRules secondSubJurisdictionalTaxRules;

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
        nonUsaAddressItemsJurisdictionalRulesInjector = new NonUsaAddressItemsJurisdictionalRulesInjector();
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());

        firstSubJurisdictionalTaxRules = testUtilities.createCitySalesTaxRules();
        secondSubJurisdictionalTaxRules = testUtilities.createCitySalesTaxRules()
                .withAbbreviation("abbreviationToRemove")
                .withName("nameToRemove");

        Map<String, SubJurisdictionalTaxRules> subJurisdictionalTaxRulesMap = new HashMap<>() {{
            put(firstSubJurisdictionalTaxRules.getName(), firstSubJurisdictionalTaxRules);
            put(secondSubJurisdictionalTaxRules.getName(), secondSubJurisdictionalTaxRules);
        }};

        jurisdictionalTaxRules = testUtilities.createJurisdictionalTaxRules().withRegions(subJurisdictionalTaxRulesMap);
    }

    @Test
    void extractCityIfExists_CityExistsInRulesMap_ReturnsModifiedRules() {
        // Given
        JurisdictionalTaxRules expectedRules = jurisdictionalTaxRules.withRegions(Map.of(firstSubJurisdictionalTaxRules.getName(), firstSubJurisdictionalTaxRules));

        // When
        JurisdictionalTaxRules actualRules = nonUsaAddressItemsJurisdictionalRulesInjector.extractRegionIfExists(jurisdictionalTaxRules, firstSubJurisdictionalTaxRules.getName());

        // Then
        Assertions.assertEquals(expectedRules, actualRules);
    }


}