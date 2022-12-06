package com.complyt.domain.customer.exemption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassificationTest {

    private Classification classification;

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
        String expectedString = "Classification(code=code, description=description)";

        // When
        String actualString = classification.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}