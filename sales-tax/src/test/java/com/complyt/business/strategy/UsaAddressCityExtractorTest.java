package com.complyt.business.strategy;


import com.complyt.business.strategy.items_jurisdictional_rules_injection.UsaAddressItemsJurisdictionalRulesInjector;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.SubJurisdictionalTaxRules;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UsaAddressCityExtractorTest {

    UsaAddressItemsJurisdictionalRulesInjector usaAddressItemsJurisdictionalRulesInjector;
    UnitTestUtilities testUtilities;

    JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    SubJurisdictionalTaxRules firstSubJurisdictionalTaxRules;
    SubJurisdictionalTaxRules secondSubJurisdictionalTaxRules;

    @BeforeEach
    void setUp() {
        usaAddressItemsJurisdictionalRulesInjector = new UsaAddressItemsJurisdictionalRulesInjector();
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

        jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules().withCities(subJurisdictionalTaxRulesMap);
    }

    @Test
    void extractCityIfExists_CityExistsInRulesMap_ReturnsModifiedRules() {
        // Given
        JurisdictionalSalesTaxRules expectedRules = jurisdictionalSalesTaxRules.withCities(Map.of(firstSubJurisdictionalTaxRules.getName(), firstSubJurisdictionalTaxRules));

        // When
        JurisdictionalSalesTaxRules actualRules = usaAddressItemsJurisdictionalRulesInjector.extractCityIfExists(jurisdictionalSalesTaxRules, firstSubJurisdictionalTaxRules.getName());

        // Then
        Assertions.assertEquals(expectedRules, actualRules);
    }

    @Test
    void extractCityIfExists_CityDoesntInRulesMap_ReturnsModifiedRules() {
        // Given
        JurisdictionalSalesTaxRules expectedRules = jurisdictionalSalesTaxRules.withCities(null);

        // When
        JurisdictionalSalesTaxRules actualRules = usaAddressItemsJurisdictionalRulesInjector.extractCityIfExists(jurisdictionalSalesTaxRules, "NonExistingCity");

        // Then
        Assertions.assertEquals(expectedRules, actualRules);
    }


}