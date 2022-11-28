package com.complyt.v1.mappers;

import com.complyt.domain.State;
import com.complyt.v1.model.StateDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
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
