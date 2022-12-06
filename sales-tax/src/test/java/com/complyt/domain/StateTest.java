package com.complyt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StateTest {

    private State state;

    @BeforeEach
    void setup() {
        state = new State("CA", "02", "California");
    }

    @Test
    void Equals_sameState_ReturnsTrue() {
        // Given
        State givenState = new State("CA", "02", "California");

        // When
        boolean isEquals = state.equals(givenState);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "State(abbreviation=CA, code=02, name=California)";

        // When
        String actualString = state.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}