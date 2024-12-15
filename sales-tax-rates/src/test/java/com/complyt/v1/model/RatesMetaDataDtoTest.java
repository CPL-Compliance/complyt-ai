package com.complyt.v1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RatesMetaDataDtoTest {
    private final BigDecimal cityDistrictRate = new BigDecimal("1.5");
    private final BigDecimal countyDistrictRate = new BigDecimal("2.5");
    private RatesMetaDataDto ratesMetaDataDto;

    @BeforeEach
    void setup() {
        ratesMetaDataDto = new RatesMetaDataDto(cityDistrictRate, countyDistrictRate);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "RatesMetaDataDto[" +
                "cityDistrictRate=" + cityDistrictRate +
                ", countyDistrictRate=" + countyDistrictRate +
                "]";

        // When
        String actualString = ratesMetaDataDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameRatesMetaDataDto_ReturnTrue() {
        // Given
        RatesMetaDataDto givenRatesMetaDataDto = new RatesMetaDataDto(cityDistrictRate, countyDistrictRate);

        // When
        boolean isEquals = ratesMetaDataDto.equals(givenRatesMetaDataDto);

        // Then
        assertTrue(isEquals);
    }
}
