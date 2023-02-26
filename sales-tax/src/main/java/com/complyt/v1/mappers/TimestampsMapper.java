package com.complyt.v1.mappers;

import com.complyt.domain.timestamps.Timestamps;
import com.complyt.v1.models.timestamps.TimestampsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface TimestampsMapper {
    TimestampsMapper INSTANCE = Mappers.getMapper(TimestampsMapper.class);


    @Mapping(target = "createdDate", source = "timestamps.createdDate", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updatedDate", source = "timestamps.updatedDate", qualifiedByName = "localDateTimeToString")
    TimestampsDto timestampsTotimestampsDto(Timestamps timestamps);
    @Mapping(target = "createdDate", source = "timestampsDto.createdDate", qualifiedByName="parseStringToLocalDateTime")
    @Mapping(target = "updatedDate", source = "timestampsDto.updatedDate", qualifiedByName="parseStringToLocalDateTime")
    Timestamps timestampsDtoTotimestamps(TimestampsDto timestampsDto);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime.toString();
    }

    @Named("parseStringToLocalDateTime")
    default LocalDateTime ComplytTimestamp(String dateAsString) {
        try {
            LocalDateTime parsedLocalDate = LocalDate.parse(dateAsString, DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0);
            //log.debug("Input received as a LocalDate: " + parsedLocalDate);

            return parsedLocalDate;
        } catch (Exception ignore) {
        }
        try {
            LocalDateTime parsedLocalDateTime = LocalDateTime.parse(dateAsString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            //log.debug("Input received as a LocalDateTime: " + parsedLocalDateTime);

            return parsedLocalDateTime;
        } catch (Exception ignore) {
        }
        try {
            ZonedDateTime zonedDate = ZonedDateTime.parse(dateAsString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
            LocalDateTime parsedDateWithOffset = LocalDateTime.ofInstant(zonedDate.toInstant(), ZoneOffset.UTC);
            //log.debug("Input received as a ZonedDateTime: " + zonedDate);

            return parsedDateWithOffset;
        } catch (Exception e) {
            //log.debug("Date has been received in invalid format : " + dateAsString);
        }
        return null;
    }
}
