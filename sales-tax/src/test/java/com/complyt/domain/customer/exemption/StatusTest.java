package com.complyt.domain.customer.exemption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusTest {

    private Status status;

    @BeforeEach
    void setup() {
        status = new Status("code", "name");
    }

    @Test
    void Equals_sameStatus_ReturnsTrue() {
        // Given
        Status givenStatus = new Status("code", "name");

        // When
        boolean isEquals = status.equals(givenStatus);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "Status(code=" + status.getCode() +
                ", name=" + status.getName() + ")";

        // When
        String actualString = status.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}