package com.complyt.business.address;

import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.MandatoryAddress;
import com.complyt.domain.transaction.MatchedAddressData;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

public class CountryIsUsaCheckerTest {
    UnitTestUtilities testUtilities;
    Address usaAddress;
    ShippingAddress shippingAddress;
    MandatoryAddress mandatoryAddress;



    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        usaAddress = testUtilities.createUsaAddress();
        shippingAddress = new ShippingAddress(usaAddress.country(), usaAddress.country(), usaAddress.county(), usaAddress.country(), usaAddress.street(), usaAddress.zip(), usaAddress.region(), usaAddress.isPartial(), null);
        mandatoryAddress = new MandatoryAddress(usaAddress.city(),usaAddress.country(), usaAddress.county(), usaAddress.state(), usaAddress.street(), usaAddress.region(), usaAddress.zip(), true);
    }

    @Test
    void isCountryUsa_ReturnsTrue() {
        // Given + When
        boolean isCountryUsa = CountryIsUsaChecker.isCountryUsa(shippingAddress);

        // Then
        Assertions.assertTrue(isCountryUsa);
    }

    @Test
    void isCountryUsa_mandatoryAddress_ReturnsTrue() {
        // Given + When
        boolean isCountryUsa = CountryIsUsaChecker.isCountryUsa(mandatoryAddress);

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
    void isCountryUsa_mandatoryAddress_ReturnsFalse() {
        // Given + When
        MandatoryAddress addressWithNonExistingCountry = mandatoryAddress.withCountry("Non Existing Country");
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
    void isCountryUsa_NullMandatoryAddressPassed_ThrowsNullPointerException() {
        // Given + When
        MandatoryAddress nullAddress = null;

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