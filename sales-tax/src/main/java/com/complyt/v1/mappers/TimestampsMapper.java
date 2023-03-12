package com.complyt.v1.mappers;

import com.complyt.domain.timestamps.Timestamps;
import com.complyt.v1.error_messages.DateErrorMessages;
import com.complyt.v1.models.timestamps.TimestampsDto;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, uses = StringLocalDateTimeMapper.class)
public interface TimestampsMapper {
    TimestampsMapper INSTANCE = Mappers.getMapper(TimestampsMapper.class);
    Logger log = LoggerFactory.getLogger(TimestampsMapper.class);


    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updatedDate", source = "updatedDate", qualifiedByName = "localDateTimeToString")
    TimestampsDto timestampsTotimestampsDto(Timestamps timestamps);
    
    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName="parseStringToLocalDateTime")
    @Mapping(target = "updatedDate", source = "updatedDate", qualifiedByName="parseStringToLocalDateTime")
    Timestamps timestampsDtoTotimestamps(TimestampsDto timestampsDto);
}
