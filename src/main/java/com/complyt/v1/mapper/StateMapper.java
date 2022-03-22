package com.complyt.v1.mapper;

import com.complyt.domain.State;
import com.complyt.v1.model.StateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StateMapper {

    StateMapper INSTANCE = Mappers.getMapper(StateMapper.class);

    @Mapping(source = "salesTaxRate", target = "salesTaxRate")
    @Mapping(source = "name", target = "name")
    StateDto stateToStateDto(State state);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "salesTaxRate", source = "salesTaxRate")
    State stateDtoToState(StateDto stateDto);
}