package com.complyt.v1.mapper;

import com.complyt.domain.State;

import com.complyt.v1.model.StateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StateMapper {
    StateMapper INSTANCE = Mappers.getMapper( StateMapper.class );

    @Mappings ({
            @Mapping(source = "state.salesTaxRate", target = "salesTaxRate"),
            @Mapping(source = "state.name", target = "name")
    })
    StateDto stateToStateDto(State state);

//    @Mappings({
//            @Mapping(target="name", source="stateDto.name"),
//            @Mapping(target="salesTaxRate", source="stateDto.salesTaxRate")
//    })
//    State employeeDTOtoEmployee(StateDto stateDto);
}