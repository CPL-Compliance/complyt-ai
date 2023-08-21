package com.complyt.domain.sales_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RatesMetaDataTest {

    RatesMetaData ratesMetaData;

    @BeforeEach
    void setUp() {
        ratesMetaData = new RatesMetaData(new BigDecimal("0.01"), new BigDecimal("0.01"));
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "RatesMetaData[cityDistrictRate=" + ratesMetaData.cityDistrictRate() +
                ", countyDistrictRate=" + ratesMetaData.countyDistrictRate() + "]";

        // When
        String actualString = ratesMetaData.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}
