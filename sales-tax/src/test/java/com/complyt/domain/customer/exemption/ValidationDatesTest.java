package com.complyt.domain.customer.exemption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidationDatesTest {
    private ValidationDates validationDates;

    @BeforeEach
    void setup() {
        validationDates = new ValidationDates(
                LocalDateTime.of(2002, 2, 2, 2, 2, 2),
                LocalDateTime.of(2003, 3, 3, 3, 3, 3));
    }

    @Test
    void withUpdateDate_DifferentDate_ReturnValidationDates() {
        // Given
        ValidationDates expectedValidationDates = new ValidationDates(
                LocalDateTime.of(2002, 2, 2, 2, 2, 2),
                LocalDateTime.of(2004, 4, 4, 4, 4, 4));
        LocalDateTime differentDate = LocalDateTime.of(2004, 4, 4, 4, 4, 4);

        // When
        ValidationDates actualValidationDates = validationDates.withToDate(differentDate);

        // Then
        assertEquals(expectedValidationDates, actualValidationDates);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ValidationDates(fromDate=" + validationDates.getFromDate() + ", toDate=" + validationDates.getToDate() + ")";

        // When
        String actualString = validationDates.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}