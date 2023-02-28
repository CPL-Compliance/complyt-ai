package com.complyt.v1.mappers;

import com.complyt.domain.customer.exemption.ValidationDates;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.v1.models.customer.exemption.ValidationDatesDto;
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
public interface ValidationDatsMapper {
    ValidationDatsMapper INSTANCE = Mappers.getMapper(ValidationDatsMapper.class);


    @Mapping(target = "fromDate", source = "validationDates.fromDate", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "toDate", source = "validationDates.toDate", qualifiedByName = "localDateTimeToString")
    ValidationDatesDto timestampsTotimestampsDto(ValidationDates validationDates);
    @Mapping(target = "fromDate", source = "validationDatesDto.fromDate", qualifiedByName="parseStringToLocalDateTime")
    @Mapping(target = "toDate", source = "validationDatesDto.toDate", qualifiedByName="parseStringToLocalDateTime")
    ValidationDates timestampsDtoTotimestamps(ValidationDatesDto validationDatesDto);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime.toString();
    }

    @Named("parseStringToLocalDateTime")
    default LocalDateTime parseStringToLocalDateTime(String dateAsString) {
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
