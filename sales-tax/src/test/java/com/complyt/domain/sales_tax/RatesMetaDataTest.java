package com.complyt.domain.sales_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RatesMetaDataTest {

    RatesMetaData ratesMetaData;

    @BeforeEach
    void setUp() {
        ratesMetaData = new RatesMetaData(0.01f, 0.01f);
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
