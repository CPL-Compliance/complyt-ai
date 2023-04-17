package com.example.complyt.v1.model;

import com.complyt.v1.model.AddressDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddressDtoTest {

    private final String city = "City";
    private final String country = "Country";
    private final String county = "County";
    private final String state = "State";
    private final String street = "Street";
    private final String zip = "1111";
    private AddressDto addressDto;

    @BeforeEach
    void setup() {
        addressDto = new AddressDto(city, country, county, state, street, zip);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "AddressDto[city=" + city +
                ", country=" + country +
                ", county=" + county +
                ", state=" + state +
                ", street=" + street +
                ", zip=" + zip + "]";

        // When
        String actualString = addressDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withCity_differentCity_ReturnAddressDto() {
        // given
        AddressDto expectedAddressDto = new AddressDto("New York", country, county, state, street, zip);

        // When
        AddressDto actualAddressDto = addressDto.withCity("New York");

        // Then
        assertEquals(expectedAddressDto, actualAddressDto);
    }

}
