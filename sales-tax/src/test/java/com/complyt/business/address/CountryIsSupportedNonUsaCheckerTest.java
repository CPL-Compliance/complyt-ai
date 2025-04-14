package com.complyt.business.address;

import com.complyt.domain.transaction.Address;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

public class CountryIsSupportedNonUsaCheckerTest {
    UnitTestUtilities testUtilities;
    Address nonUsaAddress;

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