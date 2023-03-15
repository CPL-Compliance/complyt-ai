package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CitySalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.TestUtilities;

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

    private SalesTaxRate salesTaxRate;

    TestUtilities testUtilities;
    Address address;

    @BeforeEach
    void setup() {
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules();
        salesTaxRate = testUtilities.createSalesTaxRates();
        salesTaxRatesProvider = new SalesTaxRatesProvider(stateLevelSalesTaxRatesCalculator, cityLevelSalesTaxRatesCalculator);
        address = testUtilities.createAddress();
    }

    @Test
    void provide_CalculatesSalesTaxRatesOnlyForStateLevel_ReturnsSalesTaxRates() {
        // Given

        // When
        when(stateLevelSalesTaxRatesCalculator.calculate(jurisdictionalSalesTaxRules, salesTaxRate)).thenReturn(salesTaxRate);
        SalesTaxRate actualSalesTaxRate = salesTaxRatesProvider.provide(jurisdictionalSalesTaxRules, salesTaxRate, address);

        // Then
        assertEquals(actualSalesTaxRate, salesTaxRate);
    }

    @Test
    void provide_CityDoesNotExistInCitiesMap_ReturnsSalesTaxRatesCalculatedByStateRate() {
        // Given
        CitySalesTaxRules citySalesTaxRules = testUtilities.createCitySalesTaxRules().withTaxable(false);
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRulesWithCity = jurisdictionalSalesTaxRules.withCities(
                new HashMap<>() {{
                    put(address.getCity() + "UNEQUAL_SUFFIX", citySalesTaxRules);
                }}
        );
        SalesTaxRate expectedSalesTaxRates = salesTaxRate.withStateRate(0.5f);

        // When
        when(stateLevelSalesTaxRatesCalculator.calculate(jurisdictionalSalesTaxRulesWithCity, salesTaxRate)).thenReturn(expectedSalesTaxRates);
        SalesTaxRate actualSalesTaxRate = salesTaxRatesProvider.provide(jurisdictionalSalesTaxRulesWithCity, salesTaxRate, address);

        // Then
        assertEquals(actualSalesTaxRate, expectedSalesTaxRates);
    }

    @Test
    void provide_CalculatesSalesTaxRatesForStateAndCityLevels_ReturnsSalesTaxRates() {
        // Given
        CitySalesTaxRules citySalesTaxRules = testUtilities.createCitySalesTaxRules().withTaxable(false);
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRulesWithCity = jurisdictionalSalesTaxRules.withCities(
                new HashMap<>() {{
                    put(address.getCity(), citySalesTaxRules);
                }}
        );
        SalesTaxRate expectedSalesTaxRate = salesTaxRate.withCityRate(0);

        // When
        when(stateLevelSalesTaxRatesCalculator.calculate(jurisdictionalSalesTaxRulesWithCity, salesTaxRate)).thenReturn(salesTaxRate);
        when(cityLevelSalesTaxRatesCalculator.calculate(citySalesTaxRules, salesTaxRate)).thenReturn(salesTaxRate.withCityRate(0));

        SalesTaxRate actualSalesTaxRate = salesTaxRatesProvider.provide(jurisdictionalSalesTaxRulesWithCity, salesTaxRate, address);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void provide_NullJurisdictionalSalesTaxRulesPassed_ThrowsException() {
        // Given
        JurisdictionalSalesTaxRules nullJurisdictionalSalesTaxRules = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxRatesProvider.provide(nullJurisdictionalSalesTaxRules, salesTaxRate, address));

        // Then
        assertEquals(nullPointerException.getMessage(), "jurisdictionalSalesTaxRules is marked non-null but is null");
    }

    @Test
    void provide_NullSalesTaxRatePassed_ThrowsException() {
        // Given
        SalesTaxRate nullSalesTaxRate = null;

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
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxRatesProvider.provide(jurisdictionalSalesTaxRules, salesTaxRate, nullAddress));

        // Then
        assertEquals(nullPointerException.getMessage(), "address is marked non-null but is null");
    }

}