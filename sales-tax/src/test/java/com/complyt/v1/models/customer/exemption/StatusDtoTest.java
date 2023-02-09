package com.complyt.v1.models.customer.exemption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusDtoTest {

    private StatusDto statusDto;

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