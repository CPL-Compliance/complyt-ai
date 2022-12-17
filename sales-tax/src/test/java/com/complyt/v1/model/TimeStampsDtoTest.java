package com.complyt.v1.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeStampsDtoTest {

    private TimeStampsDto timeStampsDto;

    @BeforeEach
    void setup() {
        String createdDate = LocalDateTime.of(2002, 2, 2, 2, 2, 2).toString();
        String updatedDate = LocalDateTime.of(2003, 3, 3, 3, 3, 3).toString();
        timeStampsDto = new TimeStampsDto(
                createdDate,
                updatedDate);
    }

    @Test
    void init_DatesReceivedWithOffset_ReturnsDate() {
        // Given
        String createdDate = "2015-05-25T13:05:45-05:00";
        String updatedDate = "2015-05-25T13:05:45-05:00";
        ZonedDateTime createdDateBeforeRemovingOffSet = ZonedDateTime.parse(createdDate, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        ZonedDateTime updatedDateBeforeRemovingOffSet = ZonedDateTime.parse(updatedDate, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        LocalDateTime expectedCreatedDate = LocalDateTime.ofInstant(createdDateBeforeRemovingOffSet.toInstant(), ZoneOffset.UTC);
        LocalDateTime expectedUpdatedDate = LocalDateTime.ofInstant(updatedDateBeforeRemovingOffSet.toInstant(), ZoneOffset.UTC);

        // When
        TimeStampsDto timeStampsDto = new TimeStampsDto(createdDate,updatedDate);
        LocalDateTime actualCreatedDate = timeStampsDto.getCreatedDate();
        LocalDateTime actualUpdatedDate = timeStampsDto.getCreatedDate();

        // Then
        Assertions.assertNotNull(actualCreatedDate);
        Assertions.assertNotNull(actualUpdatedDate);
        Assertions.assertEquals(expectedCreatedDate,actualCreatedDate);
        Assertions.assertEquals(expectedUpdatedDate,actualUpdatedDate);
    }

    @Test
    void init_DatesReceivedWithHour_ReturnsDate() {
        // Given
        String createdDate = "2015-05-25T13:05:45";
        String updatedDate = "2015-05-25T13:05:45";
        LocalDateTime expectedCreatedDate = LocalDateTime.parse(createdDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime expectedUpdatedDate = LocalDateTime.parse(createdDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // When
        TimeStampsDto timeStampsDto = new TimeStampsDto(createdDate,updatedDate);
        LocalDateTime actualCreatedDate = timeStampsDto.getCreatedDate();
        LocalDateTime actualUpdatedDate = timeStampsDto.getCreatedDate();

        // Then
        Assertions.assertNotNull(actualCreatedDate);
        Assertions.assertNotNull(actualUpdatedDate);
        Assertions.assertEquals(expectedCreatedDate,actualCreatedDate);
        Assertions.assertEquals(expectedUpdatedDate,actualUpdatedDate);
    }

    @Test
    void init_DatesReceivedWithNoHour_ReturnsBeginningOfDay() {
        // Given
        String createdDate = "2015-05-25";
        String updatedDate = "2015-05-25";
        LocalDateTime expectedCreatedDate = LocalDate.parse(createdDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0);
        LocalDateTime expectedUpdatedDate = LocalDate.parse(updatedDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0);

        // When
        TimeStampsDto timeStampsDto = new TimeStampsDto(createdDate,updatedDate);
        LocalDateTime actualCreatedDate = timeStampsDto.getCreatedDate();
        LocalDateTime actualUpdatedDate = timeStampsDto.getCreatedDate();

        // Then
        Assertions.assertNotNull(actualCreatedDate);
        Assertions.assertNotNull(actualUpdatedDate);
        Assertions.assertEquals(expectedCreatedDate,actualCreatedDate);
        Assertions.assertEquals(expectedUpdatedDate,actualUpdatedDate);
    }

    @Test
    void init_DatesReceivedAsInvalidFormat_AssignsNullDated() {
        // Given
        String createdDate = "2015-05-25T13:05:45-05:00asd";
        String updatedDate = "2015-05-25T13:05:45-05:00sadda";

        // When
        TimeStampsDto timeStampsDto = new TimeStampsDto(createdDate,updatedDate);
        LocalDateTime actualCreatedDate = timeStampsDto.getCreatedDate();
        LocalDateTime actualUpdatedDate = timeStampsDto.getCreatedDate();

        // Then
        Assertions.assertNull(actualCreatedDate);
        Assertions.assertNull(actualUpdatedDate);
    }

    @Test
    void withUpdateDate_DifferentDate_ReturnTimeStampsDto() {
        // Given
        String createdDate = LocalDateTime.of(2002, 2, 2, 2, 2, 2).toString();
        String updatedDate = LocalDateTime.of(2004, 4, 4, 4, 4, 4).toString();

        TimeStampsDto expectedTimeStampsDto = new TimeStampsDto(createdDate, updatedDate);
        String differentDate = LocalDateTime.of(2004, 4, 4, 4, 4, 4).toString();

        // When
        TimeStampsDto actualTimeStampsDto = timeStampsDto.withUpdatedDate(differentDate);

        // Then
        assertEquals(expectedTimeStampsDto, actualTimeStampsDto);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "TimeStampsDto(createdDate=" + timeStampsDto.getCreatedDate() +
                ", updatedDate=" + timeStampsDto.getUpdatedDate() + ")";

        // When
        String actualString = timeStampsDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}