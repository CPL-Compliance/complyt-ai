package com.complyt.business.strategy;

import com.complyt.business.strategy.items_jurisdictional_rules_injection.NonUsaAddressItemsJurisdictionalRulesInjector;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import com.complyt.domain.sales_tax.product_classification.SubJurisdictionalTaxRules;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NonUsaAddressRegionExtractorTest {

    NonUsaAddressItemsJurisdictionalRulesInjector nonUsaAddressItemsJurisdictionalRulesInjector;
    UnitTestUtilities testUtilities;

    JurisdictionalTaxRules jurisdictionalTaxRules;
    SubJurisdictionalTaxRules firstSubJurisdictionalTaxRules;
    SubJurisdictionalTaxRules secondSubJurisdictionalTaxRules;

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