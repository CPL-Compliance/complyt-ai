package com.complyt.business.tax.gt.gt_tax_web_client;

import com.complyt.business.tax.gt.CountryLevelGtRatesCalculator;
import com.complyt.business.tax.gt.GtRatesProvider;
import com.complyt.business.tax.gt.GtTaxRatesCalculator;
import com.complyt.business.tax.gt.RegionLevelGtRatesCalculator;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import com.complyt.domain.sales_tax.product_classification.SubJurisdictionalTaxRules;
import com.complyt.domain.transaction.tax.GtAddress;
import com.complyt.domain.transaction.tax.GtRates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GtRatesProviderTest {
    GtRatesProvider gtRatesProvider;
    GtTaxRatesCalculator<JurisdictionalTaxRules> countryLevelGtRatesCalculator;
    GtTaxRatesCalculator<SubJurisdictionalTaxRules> regionLevelGtRatesCalculator;

    JurisdictionalTaxRules jurisdictionalTaxRules;
    GtRates gtRates;
    GtAddress gtAddress;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        gtRatesProvider = new GtRatesProvider(new CountryLevelGtRatesCalculator(), new RegionLevelGtRatesCalculator());
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
        jurisdictionalTaxRules = testUtilities.createJurisdictionalTaxRules()
                .withSpecialTreatment(true)
                .withCalculationType(CalculationType.FIXED)
                .withRegions(null);
        gtRates = testUtilities.createGtRates();
        gtAddress = testUtilities.createArmeniaGtAddress();
    }

    @Test
    void provide_NoRulesForRegion_ReturnsModifiedGtRates() {
        // Given
        BigDecimal expectedCountryRate = jurisdictionalTaxRules.getCalculationValue();
        BigDecimal expectedTaxRate = gtRates.regionRate().add(expectedCountryRate);
        GtRates expectedGtRates = gtRates.withCountryRate(expectedCountryRate)
                .withTaxRate(expectedTaxRate);

        // When
        GtRates actualGtRates = gtRatesProvider.provide(jurisdictionalTaxRules, gtRates, gtAddress);

        // Then
        Assertions.assertEquals(expectedGtRates, actualGtRates);
    }

    @Test
    void provide_RegionDoesNotExistInRegionsMap_ReturnsModifiedGtRates() {
        // Given
        SubJurisdictionalTaxRules subJurisdictionalTaxRules = new SubJurisdictionalTaxRules("Armenia",
                "ARM", false, false, null, "description", BigDecimal.ZERO);
        Map<String, SubJurisdictionalTaxRules> regions = new HashMap<>() {{
            put("I dont exist", subJurisdictionalTaxRules);
        }};

        JurisdictionalTaxRules jurisdictionalTaxRulesToSend = jurisdictionalTaxRules.withRegions(regions);
        BigDecimal expectedCountryRate = jurisdictionalTaxRules.getCalculationValue();
        BigDecimal expectedTaxRate = gtRates.regionRate().add(expectedCountryRate);
        GtRates expectedGtRates = gtRates.withCountryRate(expectedCountryRate)
                .withTaxRate(expectedTaxRate);

        // When
        GtRates actualGtRates = gtRatesProvider.provide(jurisdictionalTaxRulesToSend, gtRates, gtAddress);

        // Then
        Assertions.assertEquals(expectedGtRates, actualGtRates);
    }

    @Test
    void provide_IncludingRulesForRegion_ReturnsModifiedGtRates() {
        // Given
        SubJurisdictionalTaxRules subJurisdictionalTaxRules = new SubJurisdictionalTaxRules("Armenia",
                "ARM", false, false, null, "description", BigDecimal.ZERO);
        Map<String, SubJurisdictionalTaxRules> regions = new HashMap<>() {{
            put("Armenia", subJurisdictionalTaxRules);
        }};

        JurisdictionalTaxRules jurisdictionalTaxRulesToSend = jurisdictionalTaxRules.withRegions(regions).withSpecialTreatment(false);
        BigDecimal expectedRegionRate = BigDecimal.ZERO;

        GtRates expectedGtRates = gtRates.withRegionRate(expectedRegionRate);

        // When
        GtRates actualGtRates = gtRatesProvider.provide(jurisdictionalTaxRulesToSend, gtRates, gtAddress);

        // Then
        Assertions.assertEquals(expectedGtRates, actualGtRates);
    }

    @Test
    void provide_NullJurisdictionalTaxRulesPassed_ThrowsNullPointerException() {
        // Given
        JurisdictionalTaxRules nullJurisdictionalTaxRules = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            gtRatesProvider.provide(null, gtRates, gtAddress);
        });

        assertEquals(nullPointerException.getMessage(), "jurisdictionalTaxRules is marked non-null but is null");
    }

    @Test
    void provide_NullGtRatesPassed_ThrowsNullPointerException() {
        // Given
        GtRates nullGtRates = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            gtRatesProvider.provide(jurisdictionalTaxRules, null, gtAddress);
        });

        assertEquals(nullPointerException.getMessage(), "originalGtRates is marked non-null but is null");
    }

    @Test
    void provide_NullGtAddressPassed_ThrowsNullPointerException() {
        // Given
        GtAddress nullGtAddress = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            gtRatesProvider.provide(jurisdictionalTaxRules, gtRates, nullGtAddress);
        });

        assertEquals(nullPointerException.getMessage(), "gtAddress is marked non-null but is null");
    }


}