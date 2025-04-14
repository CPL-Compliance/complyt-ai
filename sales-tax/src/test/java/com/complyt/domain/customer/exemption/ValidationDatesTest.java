package com.complyt.domain.customer.exemption;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

class ValidationDatesTest {
    private ValidationDates validationDates;

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
        String expectedString = "ValidationDates(fromDate=" + validationDates.getFromDate() +
                ", toDate=" + validationDates.getToDate() + ")";

        // When
        String actualString = validationDates.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}