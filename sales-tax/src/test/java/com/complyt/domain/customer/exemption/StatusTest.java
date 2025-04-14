package com.complyt.domain.customer.exemption;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class StatusTest {

    private Status status;

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