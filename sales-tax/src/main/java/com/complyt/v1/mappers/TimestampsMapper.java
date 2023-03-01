package com.complyt.v1.mappers;

import com.complyt.domain.timestamps.Timestamps;
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

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface TimestampsMapper {
    TimestampsMapper INSTANCE = Mappers.getMapper(TimestampsMapper.class);
    Logger log = LoggerFactory.getLogger(TimestampsMapper.class);


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
    default LocalDateTime parseStringToLocalDateTime(String dateAsString) throws ParseException {
        try {
            LocalDateTime parsedLocalDate = LocalDate.parse(dateAsString, DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0);
            log.debug("Input received as a LocalDate: " + parsedLocalDate);

            return parsedLocalDate;
        } catch (Exception ignore) {
        }
        try {
            LocalDateTime parsedLocalDateTime = LocalDateTime.parse(dateAsString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            log.debug("Input received as a LocalDateTime: " + parsedLocalDateTime);

            return parsedLocalDateTime;
        } catch (Exception ignore) {
        }
        try {
            ZonedDateTime zonedDate = ZonedDateTime.parse(dateAsString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
            LocalDateTime parsedDateWithOffset = LocalDateTime.ofInstant(zonedDate.toInstant(), ZoneOffset.UTC);
            log.debug("Input received as a ZonedDateTime: " + zonedDate);

            return parsedDateWithOffset;
        } catch (Exception e) {
            log.debug("Date has been received in invalid format : " + dateAsString);
        }
        throw new ParseException("Failed on parsing string to LocalDateTime Supported formats are 'YYYY-MM-DD', 'YYYY-MM-DDTHH:mm:ssZ', and 'YYYY-MM-DDTHH:mm:ss±hh:mm' (with a valid time zone offset).",0);}
}
