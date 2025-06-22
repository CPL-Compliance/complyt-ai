package io.complyt.domain.sales_tax.complyt_sales_tax_rates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CommonAddressTest {
    CommonAddress commonAddress;



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