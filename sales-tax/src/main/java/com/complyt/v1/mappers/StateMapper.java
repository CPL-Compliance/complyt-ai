package com.complyt.v1.mappers;

import com.complyt.domain.State;
import com.complyt.v1.models.StateDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface StateMapper {

    StateMapper INSTANCE = Mappers.getMapper(StateMapper.class);

    StateDto stateToStateDto(State state);

    State stateDtoToState(StateDto stateDto);
}