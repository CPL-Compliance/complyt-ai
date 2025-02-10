package io.complyt.business.address_aligner;


import io.complyt.business.address.CountryToStandardizedCountry;
import io.complyt.business.address.UsaAbbreviations;
import io.complyt.domain.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UsaAddressShippingAddressAlignerTest {

    @InjectMocks
    UsaAddressShippingAddressAligner aligner;

    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address("Los Angeles", "USA", "Los Angeles County", "CA", "123 Main St", "90001", true);
    }

    @Test
    void align_ValidAddress_ReturnsAlignedAddress() {
        // When
        Address result = aligner.align(address);

        // Then
        assertEquals(UsaAbbreviations.DEFAULT_COUNTRY, result.country());
        assertEquals("California", result.state());
        assertEquals(address.city(), result.city());
        assertEquals(address.county(), result.county());
        assertEquals(address.street(), result.street());
        assertEquals(address.zip(), result.zip());
    }

    @Test
    void align_NullCountry_ThrowsException() {
        // When
        Address result = aligner.align(address.withState(null));

        // Then
        assertNull(result.state());
    }
}
