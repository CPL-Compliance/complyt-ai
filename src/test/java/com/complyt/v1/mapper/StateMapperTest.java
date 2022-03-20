package com.complyt.v1.mapper;

import com.complyt.domain.State;
import com.complyt.v1.model.StateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class StateMapperTest {

    @Test
    public void stateToStateDto_this_and_that() {
        // Given
        State state = new State( 0.3f, "", "", "", null );

        // Ehen
        StateDto stateDto = StateMapper.INSTANCE.stateToStateDto( state );

        // Then
        assertThat( stateDto.getSalesTaxRate() ).isEqualTo( 0.3f );
    }
}