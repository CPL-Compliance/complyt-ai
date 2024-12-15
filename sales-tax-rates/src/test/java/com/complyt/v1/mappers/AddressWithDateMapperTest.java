package com.complyt.v1.mappers;

import com.complyt.domain.Address;
import com.complyt.domain.AddressWithDate;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.model.AddressWithDateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import java.text.ParseException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AddressWithDateMapperTest {
    private AddressWithDate addressWithDate;
    private AddressWithDateDto addressWithDateDto;
    private String dateString;
    private LocalDateTime localDateTime;

    @BeforeEach
    void setUp() {
        dateString = "2023-03-17T00:00";
        localDateTime = LocalDateTime.parse(dateString);
    }

    @Test
    void addressWithDateDtoToAddressDate_AddressWithDateDto_ReturnAddressWithDate() {
        // Given
        addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia(dateString);
        addressWithDate = TestUtilities.createAddressInCaliforniaWithCreationDate(localDateTime);

        // When
        AddressWithDate actualAddressWithDate = AddressWithDateMapper.INSTANCE.addressWithDateDtoToAddressDate(addressWithDateDto);

        // Then
        assertEquals(addressWithDate, actualAddressWithDate);
    }

    @Test
    void addressWithDateDtoToAddressDate_AddressWithDateDtoNull_ReturnNull() {
        // Given
        addressWithDateDto = null;

        // When
        AddressWithDate actualAddressWithDate = AddressWithDateMapper.INSTANCE.addressWithDateDtoToAddressDate(addressWithDateDto);

        // Then
        assertNull(actualAddressWithDate);
    }

    @Test
    void addressWithDateToAddressDateDto_AddressWithDate_ReturnAddressWithDateDto() {
        // Given
        addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia(dateString);
        addressWithDate = TestUtilities.createAddressInCaliforniaWithCreationDate(localDateTime);

        // When
        AddressWithDateDto actualAddressWithDateDto = AddressWithDateMapper.INSTANCE.addressWithDateToAddressDateDto(addressWithDate);

        // Then
        assertEquals(addressWithDateDto, actualAddressWithDateDto);
    }

    @Test
    void addressWithDateToAddressDateDto_AddressWithDateNull_ReturnNull() {
        // Given
        addressWithDate = null;

        // When
        AddressWithDateDto actualAddressWithDateDto = AddressWithDateMapper.INSTANCE.addressWithDateToAddressDateDto(addressWithDate);

        // Then
        assertNull(actualAddressWithDateDto);
    }
}