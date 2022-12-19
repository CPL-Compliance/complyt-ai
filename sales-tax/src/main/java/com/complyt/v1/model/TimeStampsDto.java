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
        try {
            String parsedLocalDate = LocalDate.parse(dateAsString, DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0).toString();
            log.debug("Input received as a LocalDate: " + parsedLocalDate);
            return parsedLocalDate;
        } catch (Exception ignore) {
        }
        try {
            String parsedLocalDateTime = LocalDateTime.parse(dateAsString, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString();
            log.debug("Input received as a LocalDateTime: " + parsedLocalDateTime);
            return parsedLocalDateTime;

        } catch (Exception ignore) {
        }
        try {
            ZonedDateTime zonedDate = ZonedDateTime.parse(dateAsString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
            String parsedDateWithOffset = LocalDateTime.ofInstant(zonedDate.toInstant(), ZoneOffset.UTC).toString();
            log.debug("Input received as a ZonedDateTime: " + zonedDate);
            return parsedDateWithOffset;

        } catch (Exception ignore) {
        }
        return null;
    }

    public LocalDateTime getCreatedDate() {
        try {
            return LocalDateTime.parse(createdDate);
        }
        catch (Exception e) {
            return null;
        }
    }

    public LocalDateTime getUpdatedDate() {
        try {
            return LocalDateTime.parse(updatedDate);
        }
        catch (Exception e) {
            return null;
        }
    }
}
