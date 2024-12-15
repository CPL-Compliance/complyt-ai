package com.complyt.repositories.internal_rates.address_standardization;

import com.complyt.domain.Address;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class StandardizeAddressTest {

    private Address address;


    @InjectMocks
    private StandardizeAddress standardizeAddress;

    @BeforeEach
    void setUp() {
        address = TestUtilities.createAddressInCalifornia();
    }

    @Test
    void testStandardize_WithCountyContainingSpecialCharacters() {
        // Arrange
        address = address.withCounty("St. Menahem (City)");

        // Act
        Address standardizedAddress = standardizeAddress.standardize(address);

        // Assert
        String expectedCounty = "menahem city";
        assertEquals(expectedCounty, standardizedAddress.county());
    }

    @Test
    void testStandardize_WithCountyInUpperCase() {
        // Arrange
        address = address.withCounty("COUNTY NAME");

        // Act
        Address standardizedAddress = standardizeAddress.standardize(address);

        // Assert
        String expectedCounty = "county name"; // Lowercased and trimmed of spaces
        assertEquals(expectedCounty, standardizedAddress.county());
    }

    @Test
    void testStandardize_WithNullCounty() {
        // Arrange
        address = address.withCounty(null);

        // Act
        Address standardizedAddress = standardizeAddress.standardize(address);

        // Assert
        assertNull(standardizedAddress.county());
    }

    @Test
    void testStandardize_WithCountyAlreadyStandardized() {
        // Arrange
        address = address.withCounty("countyname");

        // Act
        Address standardizedAddress = standardizeAddress.standardize(address);

        // Assert
        assertEquals("countyname", standardizedAddress.county());
    }
}