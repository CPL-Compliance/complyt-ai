package com.complyt.business.address;

import com.complyt.domain.transaction.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CountryIsSupportedNonUsaCheckerTest {
    UnitTestUtilities testUtilities;
    Address nonUsaAddress;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        nonUsaAddress = testUtilities.createNonUsaAddress();
    }

    @Test
    void isCountrySupportedNonUsaCountry_ReturnsTrue() {
        // Given + When
        boolean isCountrySupportedNonUsaCountry = CountryIsSupportedNonUsaChecker.isCountrySupportedNonUsaCountry(nonUsaAddress);

        // Then
        Assertions.assertTrue(isCountrySupportedNonUsaCountry);
    }

    @Test
    void isCountrySupportedNonUsaCountry_ReturnsFalse() {
        // Given + When
        Address addressWithNonExistingCountry = nonUsaAddress.withCountry("Non Existing Country");
        boolean isCountrySupportedNonUsaCountry = CountryIsSupportedNonUsaChecker.isCountrySupportedNonUsaCountry(addressWithNonExistingCountry);

        // Then
        Assertions.assertFalse(isCountrySupportedNonUsaCountry);
    }

    @Test
    void isCountrySupportedNonUsaCountry_NullAddressPassed_ThrowsNullPointerException() {
        // Given + When
        Address nullAddress = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> CountryIsSupportedNonUsaChecker.isCountrySupportedNonUsaCountry(nullAddress));

        // Then
        assertEquals(nullPointerException.getMessage(), "address is marked non-null but is null");
    }

    @Test
    void isCountrySupportedNonUsaCountry_AddressWithNullCountryPassed_ThrowsNullPointerException() {
        // Given + When
        String nullCountry = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> CountryIsSupportedNonUsaChecker.isCountrySupportedNonUsaCountry(nullCountry));

        // Then
        assertEquals(nullPointerException.getMessage(), "country is marked non-null but is null");
    }

}