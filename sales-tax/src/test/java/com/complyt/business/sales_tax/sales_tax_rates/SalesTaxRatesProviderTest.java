package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.CitySalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.transaction.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalesTaxRatesProviderTest {

    @InjectMocks
    SalesTaxRatesProvider salesTaxRatesProvider;

    @Mock
    SalesTaxRatesCalculator<JurisdictionalSalesTaxRules> stateLevelSalesTaxRatesCalculator;

    @Mock
    SalesTaxRatesCalculator<CitySalesTaxRules> cityLevelSalesTaxRatesCalculator;

    private JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;

    private SalesTaxRates salesTaxRates;

    UnitTestUtilities testUtilities;
    Address address;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules();
        salesTaxRates = testUtilities.createSalesTaxRates();
        salesTaxRatesProvider = new SalesTaxRatesProvider(stateLevelSalesTaxRatesCalculator, cityLevelSalesTaxRatesCalculator);
        address = testUtilities.createAddress();
    }

    @Test
    void provide_CalculatesSalesTaxRatesOnlyForStateLevel_ReturnsSalesTaxRates() {
        // Given

        // When
        when(stateLevelSalesTaxRatesCalculator.calculate(jurisdictionalSalesTaxRules, salesTaxRates)).thenReturn(salesTaxRates);
        SalesTaxRates actualSalesTaxRate = salesTaxRatesProvider.provide(jurisdictionalSalesTaxRules, salesTaxRates, address);

        // Then
        assertEquals(actualSalesTaxRate, salesTaxRates);
    }

    @Test
    void provide_CityDoesNotExistInCitiesMap_ReturnsSalesTaxRatesCalculatedByStateRate() {
        // Given
        CitySalesTaxRules citySalesTaxRules = testUtilities.createCitySalesTaxRules().withTaxable(false);
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRulesWithCity = jurisdictionalSalesTaxRules.withCities(
                new HashMap<>() {{
                    put(address.city() + "UNEQUAL_SUFFIX", citySalesTaxRules);
                }}
        );
        SalesTaxRates expectedSalesTaxRates = salesTaxRates.withStateRate(new BigDecimal("0.05"));

        // When
        when(stateLevelSalesTaxRatesCalculator.calculate(jurisdictionalSalesTaxRulesWithCity, salesTaxRates)).thenReturn(expectedSalesTaxRates);
        SalesTaxRates actualSalesTaxRate = salesTaxRatesProvider.provide(jurisdictionalSalesTaxRulesWithCity, salesTaxRates, address);

        // Then
        assertEquals(actualSalesTaxRate, expectedSalesTaxRates);
    }

    @Test
    void provide_CalculatesSalesTaxRatesForStateAndCityLevels_ReturnsSalesTaxRates() {
        // Given
        CitySalesTaxRules citySalesTaxRules = testUtilities.createCitySalesTaxRules().withTaxable(false);
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRulesWithCity = jurisdictionalSalesTaxRules.withCities(
                new HashMap<>() {{
                    put(address.city(), citySalesTaxRules);
                }}
        );
        SalesTaxRates expectedSalesTaxRate = salesTaxRates.withCityRate(BigDecimal.ZERO);

        // When
        when(stateLevelSalesTaxRatesCalculator.calculate(jurisdictionalSalesTaxRulesWithCity, salesTaxRates)).thenReturn(salesTaxRates);
        when(cityLevelSalesTaxRatesCalculator.calculate(citySalesTaxRules, salesTaxRates)).thenReturn(salesTaxRates.withCityRate(BigDecimal.ZERO));

        SalesTaxRates actualSalesTaxRate = salesTaxRatesProvider.provide(jurisdictionalSalesTaxRulesWithCity, salesTaxRates, address);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void provide_NullJurisdictionalSalesTaxRulesPassed_ThrowsException() {
        // Given
        JurisdictionalSalesTaxRules nullJurisdictionalSalesTaxRules = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxRatesProvider.provide(nullJurisdictionalSalesTaxRules, salesTaxRates, address));

        // Then
        assertEquals(nullPointerException.getMessage(), "jurisdictionalSalesTaxRules is marked non-null but is null");
    }

    @Test
    void provide_NullSalesTaxRatePassed_ThrowsException() {
        // Given
        SalesTaxRates nullSalesTaxRate = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxRatesProvider.provide(jurisdictionalSalesTaxRules, nullSalesTaxRate, address));

        // Then
        assertEquals(nullPointerException.getMessage(), "originalSalesTaxRate is marked non-null but is null");
    }

    @Test
    void provide_NullAddressPassed_ThrowsException() {
        // Given
        Address nullAddress = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxRatesProvider.provide(jurisdictionalSalesTaxRules, salesTaxRates, nullAddress));

        // Then
        assertEquals(nullPointerException.getMessage(), "address is marked non-null but is null");
    }

}