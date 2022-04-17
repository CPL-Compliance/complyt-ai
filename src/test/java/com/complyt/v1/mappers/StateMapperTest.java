package com.complyt.v1.mappers;

import com.complyt.domain.Nexus;
import com.complyt.domain.State;
import com.complyt.v1.model.NexusDto;
import com.complyt.v1.model.StateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
class StateMapperTest {

    @Test
    public void stateToStateDto_ValidState_ValidStateDto() {
        String id = UUID.randomUUID().toString();
        double salesTaxRate = 0.5;
        String abbreviation = "Abbreviation";
        String code = "Code";
        String name = "Name";
        List<Nexus> nexuses = null;
        List<NexusDto> nexuseDtos = null;

        // Given
        State state = new State(id, salesTaxRate, abbreviation, code, name, nexuses);

        // When
        StateDto stateDto = StateMapper.INSTANCE.stateToStateDto(state);

        // Then
        assertThat(stateDto).isNotNull();
        assertThat(stateDto.getName()).isEqualTo(name);
        assertThat(stateDto.getSalesTaxRate()).isEqualTo(salesTaxRate);
        assertThat(stateDto.getAbbreviation()).isEqualTo(abbreviation);
        assertThat(stateDto.getCode()).isEqualTo(code);
        assertThat(stateDto.getNexuses()).isEqualTo(nexuseDtos);
    }

    @Test
    public void stateDtoToState_ValidStateDto_ValidState() {
        // Given
        String name = "Name";
        double salesTaxRate = 0.5;
        String abbreviation = "Abbreviation";
        String code = "Code";
        List<NexusDto> nexusDtos = null;
        StateDto stateDto = new StateDto(salesTaxRate, abbreviation, code, name, nexusDtos);

        // When
        State state = StateMapper.INSTANCE.stateDtoToState(stateDto);

        // Then
        assertThat(state).isNotNull();
        assertThat(state.getName()).isEqualTo(name);
        assertThat(state.getSalesTaxRate()).isEqualTo(salesTaxRate);
        assertThat(state.getAbbreviation()).isEqualTo(abbreviation);
        assertThat(state.getCode()).isEqualTo(code);
        assertThat(ObjectUtils.isEmpty(state.getId())).isEqualTo(true);
    }

    @Test
    public void stateDtoToState_StateDtoIsNull_StateIsNull() {
        // Given
        StateDto stateDto = null;

        // When
        State state = StateMapper.INSTANCE.stateDtoToState(stateDto);

        // Then
        assertThat(state).isNull();
    }

    @Test
    public void stateToStateDto_StateIsNull_StateDtoIsNull() {
        // Given
        State state = null;

        // When
        StateDto stateDto = StateMapper.INSTANCE.stateToStateDto(state);

        // Then
        assertThat(stateDto).isNull();
    }
}