package com.complyt.v1.mappers;

import com.complyt.domain.State;
import com.complyt.v1.model.StateDto;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StateMapper {

    StateMapper INSTANCE = Mappers.getMapper(StateMapper.class);

    StateDto stateToStateDto(State state);
    State stateDtoToState(StateDto stateDto);
}