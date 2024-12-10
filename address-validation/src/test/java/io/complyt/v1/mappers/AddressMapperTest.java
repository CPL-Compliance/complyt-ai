package io.complyt.v1.mappers;

import io.complyt.domain.Address;
import io.complyt.v1.models.AddressDto;
import org.junit.jupiter.api.Test;
import test_utils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AddressMapperTest {
    
    private AddressDto addressDto = TestUtilities.getAddressDto();
    private Address address = TestUtilities.getAddress();
    

    @Test
    void addressToAddressDto_Address_returnAddressDto() {
        // When
        AddressDto resultAddressDto = AddressMapper.INSTANCE.addressToAddressDto(address);

        // Then
        assertEquals(addressDto, resultAddressDto);
    }

    @Test
    void addressDtoToAddress_AddressDto_returnAddress() {

        // When
        Address resultAddress = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);
        
        // Then
        assertEquals(address,resultAddress);
    }

    @Test
    void addressDtoToAddress_AddressDtoIsNull_returnNull() {
        // When
        Address actualAddress = AddressMapper.INSTANCE.addressDtoToAddress(null);

        // Then
        assertNull(actualAddress);
    }

    @Test
    void addressToAddressDto_addressIsNull_returnNull() {
        // When
        AddressDto actualAddressDto = AddressMapper.INSTANCE.addressToAddressDto(null);

        // Then
        assertNull(actualAddressDto);
    }
}