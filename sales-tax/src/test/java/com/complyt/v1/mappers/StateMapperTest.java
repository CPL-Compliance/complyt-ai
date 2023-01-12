package com.complyt.v1.mappers;

import com.complyt.domain.State;
import com.complyt.v1.models.StateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StateMapperTest {
    private State state;
    private StateDto stateDto;

    @BeforeEach
    void setup() {
        state = new State("CA", "02", "California");
        stateDto = new StateDto("CA", "02", "California");
    }

    @Test
    void stateToStateDto_State_returnStateDto() {

        // Given
        State givenState = state;

        // When
        StateDto actualStateDto = StateMapper.INSTANCE.stateToStateDto(givenState);

        // Then
        assertEquals(stateDto, actualStateDto);
    }

    @Test
    void stateDtoToState_StateDto_returnState() {

        // Given
        StateDto givenStateDto = stateDto;

        // When
        State actualState = StateMapper.INSTANCE.stateDtoToState(givenStateDto);

        // Then
        assertEquals(state, actualState);
    }
}
