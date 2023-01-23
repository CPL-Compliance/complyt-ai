package com.complyt.v1.models.timestamps;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.With;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode
@ToString
@With
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Schema(name = "ComplytTimestamp")
public class ComplytTimestampDto {

    @NotBlank(message = "Timestamp may not be blank")
    @Size(max = 256, message = "Timestamp may be 256 characters maximum")
    String timestamp;

    public ComplytTimestampDto(String timestamp) {
        this.timestamp = parseTimestamp(timestamp);
    }

    private String parseTimestamp(String dateAsString) {
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
        } catch (Exception e) {
            log.debug("Date has been received in invalid format : " + dateAsString);
        }
        return null;
    }

    public LocalDateTime getTimestamp() {
        try {
            return LocalDateTime.parse(timestamp);
        } catch (Exception e) {
            log.debug("Timestamp has invalid format");

            return null;
        }
    }
}
