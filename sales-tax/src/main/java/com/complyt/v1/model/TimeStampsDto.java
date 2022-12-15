package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public TimeStampsDto(String createdDateAsString, String updatedDateAsString) {
        this.createdDate = parseDate(createdDateAsString);
        this.updatedDate = parseDate(updatedDateAsString);
    }

    private String parseDate(String dateAsString) {
        try {
            LocalDateTime date = LocalDate.parse(dateAsString, DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0);
            log.debug("Input is a LocalDate: " + date);

            return date.toString();
        } catch (Exception ignored) {}
        try {
            LocalDateTime date = LocalDateTime.parse(dateAsString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            log.debug("Input is a LocalDateTime: " + date);

            return date.toString();
        } catch (Exception ignored) {}
        try {
            LocalDateTime date = ZonedDateTime.parse(dateAsString, DateTimeFormatter.ISO_ZONED_DATE_TIME).toLocalDateTime();
            log.debug("Input is a ZonedDateTime: " + date);

            return date.toString();
        } catch (Exception ignored) {}

        return null;
    }

    public LocalDateTime getCreatedDate() {
        return LocalDateTime.parse(createdDate);
    }

    public LocalDateTime getUpdatedDate() {
        return LocalDateTime.parse(updatedDate);
    }
}
