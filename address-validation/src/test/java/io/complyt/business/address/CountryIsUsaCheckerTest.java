package io.complyt.business.address;

import io.complyt.v1.models.AddressDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test_utils.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;

class CountryIsUsaCheckerTest {
    AddressDto usaAddress;

    @BeforeEach
    void setUp() {
        usaAddress = TestUtilities.getAddressDto();
    }

    @Test
    void isCountryUsa_ReturnsTrue() {
        // Given + When
        boolean isCountryUsa = CountryIsUsaChecker.isCountryUsa(usaAddress);

        // Then
        Assertions.assertTrue(isCountryUsa);
    }

    @Test
    void isCountryUsa_ReturnsFalse() {
        // Given + When
        AddressDto addressWithNonExistingCountry = usaAddress.withCountry("Non Existing Country");
        boolean isCountryUsa = CountryIsUsaChecker.isCountryUsa(addressWithNonExistingCountry);

        // Then
        Assertions.assertFalse(isCountryUsa);
    }

    @Test
    void isCountryUsa_NullAddressPassed_ThrowsNullPointerException() {
        // Given + When
        AddressDto nullAddressDto = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> CountryIsUsaChecker.isCountryUsa(nullAddressDto));

        // Then
        assertEquals(nullPointerException.getMessage(), "address is marked non-null but is null");
    }

    @Test
    void isCountryUsa_AddressWithNullCountryPassed_ThrowsNullPointerException() {
        // Given + When
        String nullCountry = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> CountryIsUsaChecker.isCountryUsa(nullCountry));

        // Then
        assertEquals(nullPointerException.getMessage(), "country is marked non-null but is null");
    }
}