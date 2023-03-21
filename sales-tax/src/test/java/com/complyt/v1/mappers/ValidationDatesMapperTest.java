package com.complyt.v1.mappers;

import com.complyt.domain.customer.exemption.ValidationDates;
import com.complyt.v1.models.customer.exemption.ValidationDatesDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ut.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationDatesMapperTest {
    TestUtilities testUtilities;
    private ValidationDates validationDates;
    private ValidationDatesDto validationDatesDto;

    @BeforeEach
    void setup() {
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        validationDates = testUtilities.createValidationDates();
        validationDatesDto = testUtilities.createValidationDatesDto();
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
