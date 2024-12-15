package com.complyt.v1.model.internal_sales_tax_rates_dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InternalAddressDtoTest {
    private final String state = "California";
    private final String county = "Los Angeles";
    private final String city = "Los Angeles";
    private final String local = "Downtown";
    private InternalAddressDto internalAddressDto;

    @BeforeEach
    void setup() {
        internalAddressDto = new InternalAddressDto(state, county, city, false, "11111", 0,0);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "InternalAddressDto[" +
                "state=California, " +
                "county=Los Angeles, " +
                "city=Los Angeles, " +
                "isUnincorporated=false, " +
                "zip=11111, " +
                "lowerPlusFourDigits=0, " +
                "upperPlusFourDigits=0]";

        // When
        String actualString = internalAddressDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameInternalAddressDto_ReturnTrue() {
        // Given
        InternalAddressDto givenInternalAddressDto = new InternalAddressDto(state, county, city, false, "11111", 0,0);

        // When
        boolean isEquals = internalAddressDto.equals(givenInternalAddressDto);

        // Then
        assertTrue(isEquals);
    }
}