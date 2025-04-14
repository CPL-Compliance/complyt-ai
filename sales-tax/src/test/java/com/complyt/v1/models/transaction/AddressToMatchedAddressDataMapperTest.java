package com.complyt.v1.models.transaction;

import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.MandatoryAddress;
import com.complyt.domain.transaction.MatchedAddressData;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import static org.junit.jupiter.api.Assertions.*;

class AddressToMatchedAddressDataMapperTest {

    Address address = UnitTestUtilities.createAddressInCalifornia();
    MandatoryAddressDto mandatoryAddressDto = new MandatoryAddressDto(address.city(), address.country(), address.county(), address.state(), address.street(), address.region(), address.zip(), address.isPartial());
    private final AddressToMatchedAddressDataMapper mapper = AddressToMatchedAddressDataMapper.INSTANCE;

    @Test
    void addressToMandatoryAddress_validAddress_mapsCorrectly() {
        // When
        MandatoryAddress mappedAddress = mapper.addressToMandatoryAddress(address);

        // Then
        assertNotNull(mappedAddress);
        assertEquals(mandatoryAddressDto.city(), mappedAddress.city());
        assertEquals(mandatoryAddressDto.country(), mappedAddress.country());
        assertEquals(mandatoryAddressDto.county(), mappedAddress.county());
        assertEquals(mandatoryAddressDto.state(), mappedAddress.state());
        assertEquals(mandatoryAddressDto.street(), mappedAddress.street());
        assertEquals(mandatoryAddressDto.region(), mappedAddress.region());
        assertEquals(mandatoryAddressDto.zip(), mappedAddress.zip());
        assertEquals(mandatoryAddressDto.isPartial(), mappedAddress.isPartial());
    }

    @Test
    void addressToMandatoryAddress_nullAddress_returnsNull() {
        // Given
        Address nullAddress = null;

        // When
        MandatoryAddress mappedAddress = mapper.addressToMandatoryAddress(nullAddress);

        // Then
        assertNull(mappedAddress);
    }

    @Test
    void addressToMatchedAddressData_validAddress_mapsCorrectlyWithNullScoring() {
        // When
        MatchedAddressData matchedAddressData = mapper.addressToMatchedAddressData(address);

        // Then
        assertNotNull(matchedAddressData);
        assertNotNull(matchedAddressData.address());
        assertNull(matchedAddressData.scoring());  // Scoring should be null
        assertEquals(mandatoryAddressDto.city(), matchedAddressData.address().city());
        assertEquals(mandatoryAddressDto.country(), matchedAddressData.address().country());
        assertEquals(mandatoryAddressDto.county(), matchedAddressData.address().county());
        assertEquals(mandatoryAddressDto.state(), matchedAddressData.address().state());
        assertEquals(mandatoryAddressDto.street(), matchedAddressData.address().street());
        assertEquals(mandatoryAddressDto.region(), matchedAddressData.address().region());
        assertEquals(mandatoryAddressDto.zip(), matchedAddressData.address().zip());
        assertEquals(mandatoryAddressDto.isPartial(), matchedAddressData.address().isPartial());
    }

    @Test
    void addressToMatchedAddressData_nullAddress_returnsNull() {
        // Given
        Address nullAddress = null;

        // When
        MatchedAddressData matchedAddressData = mapper.addressToMatchedAddressData(nullAddress);

        // Then
        assertNull(matchedAddressData);
    }
}
