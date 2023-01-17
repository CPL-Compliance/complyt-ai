package com.complyt.v1.model.customer.exemption;

import com.complyt.v1.model.timestamps.ComplytTimestampDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidationDatesDtoTest {
    private ValidationDatesDto validationDatesDto;

    @BeforeEach
    void setup() {
        validationDatesDto = new ValidationDatesDto(
                new ComplytTimestampDto("2002-02-02T02:02:02"),
                new ComplytTimestampDto("2004-04-04T04:04:04"));
    }

    @Test
    void withUpdateDate_DifferentDate_ReturnValidationDatesDto() {
        // Given
        ValidationDatesDto expectedValidationDatesDto = new ValidationDatesDto(
                new ComplytTimestampDto("2002-02-02T02:02:02"),
                new ComplytTimestampDto("2004-04-04T04:04:04"));
        ComplytTimestampDto differentDate = new ComplytTimestampDto("2004-04-04T04:04:04");

        // When
        ValidationDatesDto actualValidationDatesDto = validationDatesDto.withToDate(differentDate);

        // Then
        assertEquals(expectedValidationDatesDto, actualValidationDatesDto);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ValidationDatesDto(fromDate=" + validationDatesDto.getFromDate() +
                ", toDate=" + validationDatesDto.getToDate() + ")";

        // When
        String actualString = validationDatesDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}