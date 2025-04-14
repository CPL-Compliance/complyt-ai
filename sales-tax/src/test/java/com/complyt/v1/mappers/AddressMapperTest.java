package com.complyt.v1.mappers;

import com.complyt.domain.transaction.Address;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.transaction.MandatoryAddressDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockStatic;

class AddressMapperTest {

    private MandatoryAddressDto mandatoryAddressDto;
    private Address address;
    UnitTestUtilities testUtilities;

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
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        mandatoryAddressDto = testUtilities.createMandatoryAddressDto();
        address = testUtilities.createAddress();
    }

    @Test
    void addressToMandatoryAddressDto_Address_returnMandatoryAddressDto() {
        // Given
        Address givenAddress = address;

        // When
        MandatoryAddressDto actualMandatoryAddressDto = AddressMapper.INSTANCE.addressToMandatoryAddressDto(givenAddress);

        // Then
        assertEquals(mandatoryAddressDto, actualMandatoryAddressDto);
    }

    @Test
    void mandatoryAddressDtoToAddress_MandatoryAddressDto_returnAddress() {
        // Given
        MandatoryAddressDto givenMandatoryAddressDto = mandatoryAddressDto;

        // When
        Address actualAddress = AddressMapper.INSTANCE.mandatoryAddressDtoToAddress(givenMandatoryAddressDto);

        // Then
        assertEquals(address, actualAddress);
    }

    @Test
    void mapping_NullState_ReturnNull() {
        // Given + When
        MandatoryAddressDto givenMandatoryAddressDto = AddressMapper.INSTANCE.addressToMandatoryAddressDto(null);
        Address givenAddress = AddressMapper.INSTANCE.mandatoryAddressDtoToAddress(null);

        // Then
        assertNull(givenMandatoryAddressDto);
        assertNull(givenAddress);
    }

    @Test
    void addressToMandatoryAddressDto_AddressIsNull_returnNull() {
        // Given
        Address givenAddress = null;

        // When
        MandatoryAddressDto actualMandatoryAddressDto = AddressMapper.INSTANCE.addressToMandatoryAddressDto(givenAddress);

        // Then
        assertNull(actualMandatoryAddressDto);
    }

    @Test
    void mandatoryAddressDtoToAddress_MandatoryAddressDtoIsNull_returnNull() {
        // Given
        MandatoryAddressDto givenMandatoryAddressDto = null;

        // When
        Address actualAddress = AddressMapper.INSTANCE.mandatoryAddressDtoToAddress(givenMandatoryAddressDto);

        // Then
        assertNull(actualAddress);
    }

}