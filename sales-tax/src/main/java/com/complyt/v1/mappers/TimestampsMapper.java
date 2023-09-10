package com.complyt.v1.mappers;

import com.complyt.domain.timestamps.Timestamps;
import com.complyt.v1.models.TimestampsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, uses = StringToLocalDateTimeMapper.class)
public interface TimestampsMapper {
    TimestampsMapper INSTANCE = Mappers.getMapper(TimestampsMapper.class);

    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName = "parseLocalDateTimeToString")
    @Mapping(target = "updatedDate", source = "updatedDate", qualifiedByName = "parseLocalDateTimeToString")
    TimestampsDto timestampsTotimestampsDto(Timestamps timestamps);

    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName = "parseStringToLocalDateTime")
    @Mapping(target = "updatedDate", source = "updatedDate", qualifiedByName = "parseStringToLocalDateTime")
    Timestamps timestampsDtoTotimestamps(TimestampsDto timestampsDto);
}