package com.complyt.v1.model.customer.exemption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidationDatesDtoTest {
    private ValidationDatesDto validationDatesDto;

    @BeforeEach
    void setup() {
        validationDatesDto = new ValidationDatesDto(
                LocalDateTime.of(2002, 2, 2, 2, 2, 2),
                LocalDateTime.of(2003, 3, 3, 3, 3, 3));
    }

    @Test
    void withUpdateDate_DifferentDate_ReturnValidationDatesDto() {
        // Given
        ValidationDatesDto expectedValidationDatesDto = new ValidationDatesDto(
                LocalDateTime.of(2002, 2, 2, 2, 2, 2),
                LocalDateTime.of(2004, 4, 4, 4, 4, 4));
        LocalDateTime differentDate = LocalDateTime.of(2004, 4, 4, 4, 4, 4);

        // When
        ValidationDatesDto actualValidationDatesDto = validationDatesDto.withToDate(differentDate);

        // Then
        assertEquals(expectedValidationDatesDto, actualValidationDatesDto);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ValidationDatesDto(fromDate=" + validationDatesDto.getFromDate() + ", toDate=" + validationDatesDto.getToDate() + ")";

        // When
        String actualString = validationDatesDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}