package com.complyt.v1.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StateDtoTest {

    private StateDto stateDto;

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