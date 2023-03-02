package com.complyt.v1.mappers;

import com.complyt.domain.customer.exemption.ValidationDates;
import com.complyt.v1.error_messages.DateErrorMessages;
import com.complyt.v1.models.customer.exemption.ValidationDatesDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ValidationDatesMapper {
    ValidationDatesMapper INSTANCE = Mappers.getMapper(ValidationDatesMapper.class);


    @Mapping(target = "fromDate", source = "validationDates.fromDate", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "toDate", source = "validationDates.toDate", qualifiedByName = "localDateTimeToString")
    ValidationDatesDto timestampsToTimestampsDto(ValidationDates validationDates);

    @Mapping(target = "fromDate", source = "validationDatesDto.fromDate", qualifiedByName = "parseStringToLocalDateTime")
    @Mapping(target = "toDate", source = "validationDatesDto.toDate", qualifiedByName = "parseStringToLocalDateTime")
    ValidationDates timestampsDtoToTimestamps(ValidationDatesDto validationDatesDto);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime.toString();
    }

    @Named("parseStringToLocalDateTime")
    default LocalDateTime parseStringToLocalDateTime(String dateAsString) throws ParseException {
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
        throw new ParseException("Failed on parsing string to LocalDateTime " + DateErrorMessages.wrong_format_error_message, 0);
    }
}
