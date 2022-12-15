package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@EqualsAndHashCode
@ToString
@With
@Slf4j
@Schema(name = "TimeStamps")
public class TimeStampsDto {

    private String createdDate;
    private String updatedDate;

    public TimeStampsDto(String createdDate, String updatedDate) {
        this.createdDate = parseDate(createdDate);
        this.updatedDate = parseDate(updatedDate);
    }

    private String parseDate(String dateAsString) {
        String parsedDate = null;
        try {
            parsedDate = LocalDate.parse(dateAsString, DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0).toString();
            log.debug("Input received as a LocalDate: " + parsedDate);

        } catch (Exception ignore) {
        }
        try {
            parsedDate = LocalDateTime.parse(dateAsString, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString();
            log.debug("Input received as a LocalDateTime: " + parsedDate);

        } catch (Exception ignore) {
        }
        try {
            ZonedDateTime zonedDate = ZonedDateTime.parse(dateAsString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
            parsedDate = LocalDateTime.ofInstant(zonedDate.toInstant(), ZoneOffset.UTC).toString();
            log.debug("Input received as a ZonedDateTime: " + zonedDate);

        } catch (Exception ignore) {
        }
        return parsedDate;
    }

    public LocalDateTime getCreatedDate() {
        return LocalDateTime.parse(createdDate);
    }

    public LocalDateTime getUpdatedDate() {
        return LocalDateTime.parse(updatedDate);
    }
}
