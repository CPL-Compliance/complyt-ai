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

class ClassificationTest {

    private Classification classification;

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
        classification = new Classification("code", "description");
    }

    @Test
    void Equals_sameClassification_ReturnTrue() {
        // Given
        Classification givenClassification = new Classification("code", "description");

        // When
        boolean isEquals = classification.equals(givenClassification);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "Classification(code=" + classification.getCode() +
                ", description=" + classification.getDescription() + ")";

        // When
        String actualString = classification.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}