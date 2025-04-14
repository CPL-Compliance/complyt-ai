package com.complyt.v1.models.customer.exemption;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class StatusDtoTest {

    private StatusDto statusDto;

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
        statusDto = new StatusDto("code", "name");
    }

    @Test
    void Equals_sameStatusDto_ReturnTrue() {
        // Given
        StatusDto givenStatusDto = new StatusDto("code", "name");

        // When
        boolean isEquals = statusDto.equals(givenStatusDto);

        // Then
        assertTrue(isEquals);
    }
}