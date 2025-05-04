package com.complyt.v1.mappers;

import com.complyt.domain.State;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.StateDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockStatic;

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

    @Test
    void mapping_NullState_ReturnNull() {
        // Given + When
        State givenState = StateMapper.INSTANCE.stateDtoToState(null);
        StateDto givenStateDto = StateMapper.INSTANCE.stateToStateDto(null);

        // Then
        assertNull(givenState);
        assertNull(givenStateDto);
    }
}
