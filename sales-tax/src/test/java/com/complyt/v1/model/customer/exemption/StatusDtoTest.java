package com.complyt.v1.model.customer.exemption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusDtoTest {

    private StatusDto statusDto;

    @BeforeEach void setup () {
        statusDto = new StatusDto("code","name");
    }

    @Test
    void Equals_sameStatusDto_ReturnTrue() {
        // Given
         StatusDto givenStatusDto = new StatusDto("code","name");

         // When
        boolean actualBoolean = statusDto.equals(givenStatusDto);

        // Then
        assertTrue(actualBoolean);
    }
}