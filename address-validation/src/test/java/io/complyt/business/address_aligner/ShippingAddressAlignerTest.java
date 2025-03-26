package io.complyt.business.address_aligner;


import io.complyt.business.address.UsaAbbreviations;
import io.complyt.domain.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import test_utils.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShippingAddressAlignerTest {

    @InjectMocks
    ShippingAddressAligner aligner;

    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address("Los Angeles", "USA", "Los Angeles County", "CA", "123 Main St", "90001", null, true);
    }

    @Test
    void align_ForOutsource_ValidAddress_ReturnsAlignedAddress() {
        // When
        Address result = aligner.alignForOutsource(address);

        // Then
        assertEquals(UsaAbbreviations.DEFAULT_COUNTRY, result.country());
        assertEquals("California", result.state());
        assertEquals(address.city(), result.city());
        assertEquals(address.county(), result.county());
        assertEquals(address.street(), result.street());
        assertEquals(address.zip(), result.zip());
    }

    @Test
    void align_ForOutsource_NullCountry_ThrowsException() {
        // When
        Address result = aligner.alignForOutsource(address.withState(null));

        // Then
        assertNull(result.state());
    }

    @Test
    void align_ForOutsource_NonUsaAddress_ReturnsAlignedAddressWithRegion() {
        address = new Address("Toronto", "Canada", "Ontario", null, "456 Queen St", "M5H 2N2", "ON", true);
        Address alignedAddress = aligner.alignForOutsource(address);

        assertEquals("Canada", alignedAddress.country());
        assertEquals("ON", alignedAddress.state()); // State is the region for Here qq
    }

    @Test
    void align_ForOutsource_UsaAddressWithAbbreviatedCountry_ReturnsFullAbbreviation() {
        address = new Address("New York", "US", "New York County", "NY", "789 Broadway", "10001", null, true);
        Address alignedAddress = aligner.alignForOutsource(address);

        assertEquals(UsaAbbreviations.DEFAULT_COUNTRY, alignedAddress.country());
        assertEquals("New York", alignedAddress.state());
    }

    @Test
    void align_ForOutsource_UsaAddressWithLowerCaseState_ReturnsAlignedState() {
        address = new Address("Miami", "USA", "Miami-Dade County", "fl", "101 Ocean Dr", "33139", null, true);
        Address alignedAddress = aligner.alignForOutsource(address);

        assertEquals(UsaAbbreviations.DEFAULT_COUNTRY, alignedAddress.country());
        assertEquals("fl", alignedAddress.state());
    }

    @Test
    void alignForOutsource_AddressWithNullCountryPassed_ThrowsNullPointerException() {
        // Given + When
        // Then
        assertThrows(NullPointerException.class, () -> aligner.alignForOutsource(null));
    }

    @Test
    void alignGlobalAddress_AddressWithNullCountryPassed_ThrowsNullPointerException() {
        // Given + When
        Address address = null;
        // Then
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            aligner.alignGlobalAddress(address);
        });

        // Then
        assertEquals("address " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE, exception.getMessage());
    }

    @Test
    void GlobalAddress_AddressInTheUS_ReturnsSameAddress() {
        // Given + When
        Address res = aligner.alignGlobalAddress(address);
        assertEquals(address, res);
    }

    @Test
    void GlobalAddress_AddressNotInTheUS_ReturnsAlignedAddress() {
        // Given + When
        Address expectedAddress = address.withCountry("Germany");
        Address res = aligner.alignGlobalAddress(address.withCountry("DE"));
        assertEquals(expectedAddress, res);
    }
}
