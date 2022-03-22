package com.complyt.v1.mapper;

import com.complyt.domain.Nexus;
import com.complyt.domain.State;
import com.complyt.v1.model.StateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class StateMapperTest {

    @Test
    public void stateToStateDto_ValidState_ValidStateDto() {
        String id = UUID.randomUUID().toString();
        double salesTaxRate = 0.5;
        String abbreviation = "Abbreviation";
        String code = "Code";
        String name = "Name";
        List<Nexus> nexuses = null;

        // Given
        State state = new State(id, salesTaxRate, abbreviation, code, name, nexuses);

        // When
        StateDto stateDto = StateMapper.INSTANCE.stateToStateDto(state);

        // Then
        assertThat(stateDto.getSalesTaxRate()).isEqualTo(salesTaxRate);
    }

    @Test
    public void stateToStateDto_ValidStateDto_ValidState() {
        // Given
        String name = "Name";
        double salesTaxRate = 0.5;
        StateDto stateDto = new StateDto(name, salesTaxRate);

        // When
        State state = StateMapper.INSTANCE.stateDtoToState(stateDto);

        // Then
        assertThat(state.getName()).isEqualTo(name);
        assertThat(state.getSalesTaxRate()).isEqualTo(salesTaxRate);
    }
}