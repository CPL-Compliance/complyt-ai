package com.complyt.v1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressDtoTest {

    private AddressDto addressDto;

    @BeforeEach
    void setup() {
        addressDto = new AddressDto("city", "country", "county", "state", "street", "1111");
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "AddressDto(city=city, country=country, county=county, state=state, street=street, zip=1111)";

        // When
        String actualString = addressDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withCity_differentCity_ReturnAddressDto() {
        // given
        AddressDto expectedAddressDto = new AddressDto("New York", "country", "county", "state", "street", "1111");

        // When
        AddressDto actualAddressDto = addressDto.withCity("New York");

        // Then
        assertEquals(expectedAddressDto, actualAddressDto);
    }
}