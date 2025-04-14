package com.complyt.domain.sales_tax.complyt_sales_tax_rates;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mockStatic;

class CommonAddressTest {
    CommonAddress commonAddress;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        commonAddress = new CommonAddress(
                "US",
                "California",
                "Los Angeles",
                "Santa Monica",
                true,
                true,
                "90401-1234",
                1234,
                5678,
                "Main St",
                false
        );
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "CommonAddress[country=" + commonAddress.country() +
                ", state=" + commonAddress.state() +
                ", county=" + commonAddress.county() +
                ", city=" + commonAddress.city() +
                ", isUnincorporated=" + commonAddress.isUnincorporated() +
                ", hasPlusFourZipCode=" + commonAddress.hasPlusFourZipCode() +
                ", zip=" + commonAddress.zip() +
                ", lowerPlusFourDigits=" + commonAddress.lowerPlusFourDigits() +
                ", upperPlusFourDigits=" + commonAddress.upperPlusFourDigits() +
                ", street=" + commonAddress.street() +
                ", isPartial=" + commonAddress.isPartial() +
                "]";

        // When
        String actualString = commonAddress.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withCountry_ReturnNewInstance() {
        // Given
        String newCountry = "CA";

        // When
        CommonAddress updatedAddress = commonAddress.withCountry(newCountry);

        // Then
        assertNotEquals(commonAddress, updatedAddress);
        assertEquals(newCountry, updatedAddress.country());
        assertEquals(commonAddress.state(), updatedAddress.state()); // Ensure immutability
    }

}