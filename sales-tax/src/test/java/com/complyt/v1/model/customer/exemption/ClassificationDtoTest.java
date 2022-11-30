package com.complyt.v1.model.customer.exemption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassificationDtoTest {

    private ClassificationDto classificationDto;

    @BeforeEach
    void setup () {
        classificationDto = new ClassificationDto("code","description");
    }

    @Test
    void Equals_sameClassificationDto_ReturnTrue() {
        // Given
        ClassificationDto givenClassificationDto = new ClassificationDto("code","description");

        // When
        boolean expectedBoolean = classificationDto.equals(givenClassificationDto);

        // Then
        assertTrue(expectedBoolean);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ClassificationDto(code=code, description=description)";

        // When
        String actualString = classificationDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}