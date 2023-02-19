package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.CitySalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.ObjectStub;

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
    SalesTaxRatesCalculator stateLevelSalesTaxRatesCalculator;

    @Mock
    SalesTaxRatesCalculator cityLevelSalesTaxRatesCalculator;

    private JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;

    private SalesTaxRate salesTaxRate;

    ObjectStub objectStub;
    Address address;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        jurisdictionalSalesTaxRules = objectStub.createJurisdictionalSalesTaxRules();
        salesTaxRate = objectStub.createSalesTaxRates();
        salesTaxRatesProvider = new SalesTaxRatesProvider(stateLevelSalesTaxRatesCalculator, cityLevelSalesTaxRatesCalculator);
        address = objectStub.createAddress();
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
    void provide_CalculatesSalesTaxRatesForStateAndCityLevels_ReturnsSalesTaxRates() {
        // Given
        CitySalesTaxRules citySalesTaxRules = objectStub.createCitySalesTaxRules().withTaxable(false);
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

}