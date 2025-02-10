package com.complyt.business.address;

import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.ShippingAddress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CountryIsUsaCheckerTest {
    UnitTestUtilities testUtilities;
    Address usaAddress;
    ShippingAddress shippingAddress;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        usaAddress = testUtilities.createUsaAddress();
        shippingAddress = new ShippingAddress(usaAddress.country(), usaAddress.country(), usaAddress.county(), usaAddress.country(), usaAddress.street(), usaAddress.zip(), usaAddress.region(), usaAddress.isPartial(), null);
    }

    @Test
    void isCountryUsa_ReturnsTrue() {
        // Given + When
        boolean isCountryUsa = CountryIsUsaChecker.isCountryUsa(shippingAddress);

        // Then
        Assertions.assertTrue(isCountryUsa);
    }

    @Test
    void isCountryUsa_ReturnsFalse() {
        // Given + When
        ShippingAddress addressWithNonExistingCountry = shippingAddress.withCountry("Non Existing Country");
        boolean isCountryUsa = CountryIsUsaChecker.isCountryUsa(addressWithNonExistingCountry);

        // Then
        Assertions.assertFalse(isCountryUsa);
    }

    @Test
    void isCountryUsa_NullAddressPassed_ThrowsNullPointerException() {
        // Given + When
        ShippingAddress nullAddress = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> CountryIsUsaChecker.isCountryUsa(nullAddress));

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