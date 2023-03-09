package com.complyt.v1.mappers;

import com.complyt.domain.customer.exemption.ValidationDates;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.v1.models.customer.exemption.ValidationDatesDto;
import com.complyt.v1.models.timestamps.TimestampsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationDatesMapperTest {
    private ValidationDates validationDates;
    private ValidationDatesDto validationDatesDto;
    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                LocalDateTime.now(), UUID.randomUUID().toString());
        validationDates = objectStub.createValidationDates();
        validationDatesDto = objectStub.createValidationDatesDto();
    }

    @Test
    void validationDatesToValidationDatesDto_ValidationDates_returnValidationDatesDto() {
        // Given
        ValidationDates givenValidationDates = validationDates;

        // When
        ValidationDatesDto actualValidationDatesDto = ValidationDatesMapper.INSTANCE.validationDatesToValidationDatesDto(givenValidationDates);

        // Then
        assertEquals(validationDatesDto, actualValidationDatesDto);
    }

    @Test
    void validationDatesDtoToValidationDates_ValidatinDatesDto_returnValidationDates() {
        // Given
        ValidationDatesDto givenValidationDates = validationDatesDto;

        // When
        ValidationDates actualValidationDates = ValidationDatesMapper.INSTANCE.validationDatesDtoToValidationDates(givenValidationDates);

        // Then
        assertEquals(validationDates, actualValidationDates);
    }

}
