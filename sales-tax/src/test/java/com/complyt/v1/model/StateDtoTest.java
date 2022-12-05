package com.complyt.v1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        boolean actualBoolean = stateDto.equals(givenStateDto);

        // Then
        assertTrue(actualBoolean);
    }

}