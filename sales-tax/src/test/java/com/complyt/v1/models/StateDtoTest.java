package com.complyt.v1.models;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class StateDtoTest {

    private StateDto stateDto;

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
        stateDto = new StateDto("CA", "02", "California");
    }

    @Test
    void Equals_sameStateDto_ReturnsTrue() {
        // Given
        StateDto givenStateDto = new StateDto("CA", "02", "California");

        // When
        boolean isEquals = stateDto.equals(givenStateDto);

        // Then
        assertTrue(isEquals);
    }

}