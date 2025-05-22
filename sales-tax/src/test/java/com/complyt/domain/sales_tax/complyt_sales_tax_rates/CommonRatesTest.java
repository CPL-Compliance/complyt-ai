package com.complyt.domain.sales_tax.complyt_sales_tax_rates;

import com.complyt.domain.sales_tax.RatesMetaData;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mockStatic;

class CommonRatesTest {
    CommonRates commonRates;

   

    @BeforeEach
    void setUp() {
        RatesMetaData ratesMetaData = new RatesMetaData(
                new BigDecimal("0.01"),
                new BigDecimal("0.02"),
                new BigDecimal("0.03")
        );

        commonRates = new CommonRates(
                new BigDecimal("0.05"),  // stateRate
                new BigDecimal("0.02"),  // countyRate
                new BigDecimal("0.01"),  // cityRate
                new BigDecimal("0.08"),  // combinedDistrictRate
                ratesMetaData,           // ratesMetaData
                new BigDecimal("0.03"),  // mtaRate
                new BigDecimal("0.01"),  // spdRate
                new BigDecimal("0.02"),  // otherRate
                new BigDecimal("0.09")   // taxRate
        );
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "CommonRates[stateRate=" + commonRates.stateRate() +
                ", countyRate=" + commonRates.countyRate() +
                ", cityRate=" + commonRates.cityRate() +
                ", combinedDistrictRate=" + commonRates.combinedDistrictRate() +
                ", ratesMetaData=" + commonRates.ratesMetaData() +
                ", mtaRate=" + commonRates.mtaRate() +
                ", spdRate=" + commonRates.spdRate() +
                ", otherRate=" + commonRates.otherRate() +
                ", taxRate=" + commonRates.taxRate() + "]";

        // When
        String actualString = commonRates.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withStateRate_ReturnNewInstance() {
        // Given
        BigDecimal newStateRate = new BigDecimal("0.06");

        // When
        CommonRates updatedRates = commonRates.withStateRate(newStateRate);

        // Then
        assertNotEquals(commonRates, updatedRates);
        assertEquals(newStateRate, updatedRates.stateRate());
        assertEquals(commonRates.countyRate(), updatedRates.countyRate()); // Ensure immutability
    }

    @Test
    void withRatesMetaData_ReturnNewInstance() {
        // Given
        RatesMetaData newRatesMetaData = new RatesMetaData(
                new BigDecimal("0.05"),
                new BigDecimal("0.06"),
                new BigDecimal("0.07")
        );

        // When
        CommonRates updatedRates = commonRates.withRatesMetaData(newRatesMetaData);

        // Then
        assertNotEquals(commonRates, updatedRates);
        assertEquals(newRatesMetaData, updatedRates.ratesMetaData());
        assertEquals(commonRates.stateRate(), updatedRates.stateRate()); // Ensure immutability
    }

    @Test
    void withTaxRate_ReturnNewInstance() {
        // Given
        BigDecimal newTaxRate = new BigDecimal("0.10");

        // When
        CommonRates updatedRates = commonRates.withTaxRate(newTaxRate);

        // Then
        assertNotEquals(commonRates, updatedRates);
        assertEquals(newTaxRate, updatedRates.taxRate());
        assertEquals(commonRates.mtaRate(), updatedRates.mtaRate()); // Ensure immutability
    }
}