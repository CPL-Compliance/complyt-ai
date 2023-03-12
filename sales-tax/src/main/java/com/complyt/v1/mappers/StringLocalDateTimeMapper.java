package com.complyt.v1.mappers;

import com.complyt.v1.error_messages.DateErrorMessages;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
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
public interface StringLocalDateTimeMapper {
    StringLocalDateTimeMapper INSTANCE = Mappers.getMapper(StringLocalDateTimeMapper.class);

    Logger log = LoggerFactory.getLogger(TimestampsMapper.class);


    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime dateTime) {
        if ( dateTime == null ) {
            return null;
        }

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
        throw new ParseException("Failed on parsing string to LocalDateTime " + DateErrorMessages.wrong_format_error_message, 0);
    }
}
