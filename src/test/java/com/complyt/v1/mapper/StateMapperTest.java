package com.complyt.v1.mapper;

import com.complyt.domain.State;
import com.complyt.v1.model.StateDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StateMapperTest {

    @Test
    public void shouldMapCarToDto() {
        //given
        State state = new State( 0.3f, "", "", "", null );

        //when
        StateDto stateDto = StateMapper.INSTANCE.stateToStateDto( state );

        //then
        assertThat( stateDto.getSalesTaxRate() ).isEqualTo( 0.3f );
    }
}