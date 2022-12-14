package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
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

    public TimeStampsDto(String createdDate, String updatedDate) {
        this.createdDate = parseDate(createdDate);
        this.updatedDate = parseDate(updatedDate);
    }

    private String parseDate(String date) {
        String localDateTime = null;
        try {
            LocalDateTime localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0);
            localDateTime = localDate.toString();
            log.debug("Input is a LocalDate: " + localDateTime);
        } catch (Exception e) {
            log.debug("Input is not a LocalDate ");
        }
        try {
            LocalDateTime localDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            localDateTime = localDate.toString();
            log.debug("Input is a LocalDateTime: " + localDateTime);
        } catch (Exception e) {
            log.debug("Input is not a LocalDateTime ");
        }
        try {
            LocalDateTime localDate = ZonedDateTime.parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME).toLocalDateTime();
            localDateTime = localDate.toString();
            log.debug("Input is a ZonedDateTime: " + localDateTime);

        } catch (Exception e) {
            log.debug("Input is not a zonedDateTime ");
        }
        return localDateTime;
    }

    public LocalDateTime getCreatedDate() {
        return LocalDateTime.parse(createdDate);
    }

    public LocalDateTime getUpdatedDate() {
        return LocalDateTime.parse(updatedDate);
    }
}
