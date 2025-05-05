package com.complyt.v1.models.customer.exemption;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class ClassificationDtoTest {

    private ClassificationDto classificationDto;

   

    @BeforeEach
    void setup() {
        classificationDto = new ClassificationDto("code", "description");
    }

    @Test
    void Equals_sameClassificationDto_ReturnsTrue() {
        // Given
        ClassificationDto givenClassificationDto = new ClassificationDto("code", "description");

        // When
        boolean isEquals = classificationDto.equals(givenClassificationDto);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "ClassificationDto[code=" + classificationDto.code() + ", description=" + classificationDto.description() + "]";

        // When
        String actualString = classificationDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}