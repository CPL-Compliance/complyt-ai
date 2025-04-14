package com.complyt.v1.models.customer.exemption;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

class ValidationDatesDtoTest {
    private ValidationDatesDto validationDatesDto;

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
        validationDatesDto = new ValidationDatesDto(
                "2002-02-02T02:02:02",
                "2004-04-04T04:04:04");
    }

    @Test
    void withUpdateDate_DifferentDate_ReturnValidationDatesDto() {
        // Given
        ValidationDatesDto expectedValidationDatesDto = new ValidationDatesDto(
                "2002-02-02T02:02:02",
                "2004-04-04T04:04:04");
        LocalDateTime differentDate = LocalDateTime.parse("2004-04-04T04:04:04");

        // When
        ValidationDatesDto actualValidationDatesDto = validationDatesDto.withToDate(differentDate.toString());

        // Then
        assertEquals(expectedValidationDatesDto, actualValidationDatesDto);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ValidationDatesDto[fromDate=" + validationDatesDto.fromDate() +
                ", toDate=" + validationDatesDto.toDate() + "]";

        // When
        String actualString = validationDatesDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}