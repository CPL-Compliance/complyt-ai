package com.complyt.v1.models;

import com.complyt.v1.models.timestamps.TimestampsDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TimestampsDtoTest {

    private TimestampsDto timestampsDto;

    @BeforeEach
    void setup() {
        String createdDate = "2002-02-02T02:02:02";
        String updatedDate = "2003-03-03T03:03:03";
        timestampsDto = new TimestampsDto(createdDate, updatedDate);
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
        TimestampsDto timeStampsDto = new TimestampsDto(createdDate, updatedDate);
        LocalDateTime actualCreatedDate = LocalDateTime.parse(createdDate);
        LocalDateTime actualUpdatedDate = LocalDateTime.parse(updatedDate);

        // Then
        Assertions.assertNotNull(actualCreatedDate);
        Assertions.assertNotNull(actualUpdatedDate);
        Assertions.assertEquals(expectedCreatedDate, actualCreatedDate);
        Assertions.assertEquals(expectedUpdatedDate, actualUpdatedDate);
    }

//    @Test
//    void init_InvalidFormatOfCreatedDate_CreatedDateIsSetToNull() {
//        // Given
//        String createdDate = "2015-05-25asd";
//        String updatedDate = "2015-05-25";
//
//        // When
//        TimestampsDto timeStampsDto = new TimestampsDto(createdDate, updatedDate);
//        LocalDateTime expectedCreatedDate = timeStampsDto.getCreatedDate();
//
//        // Then
//        assertNull(expectedCreatedDate);
//    }

//    @Test
//    void init_InvalidFormatOfUpdatedDate_CreatedDateIsSetToNull() {
//        // Given
//        String createdDate = "2015-05-25";
//        String updatedDate = "2015-05-25asd";
//
//        // When
//        TimestampsDto timeStampsDto = new TimestampsDto(createdDate, updatedDate);
//        LocalDateTime expectedUpdatedDate = timeStampsDto.getUpdatedDate();
//
//        // Then
//        assertNull(expectedUpdatedDate);
//    }

    @Test
    void init_DatesReceivedWithHour_ReturnsDate() {
        // Given
        String createdDate = "2015-05-25T13:05:45";
        String updatedDate = "2015-05-25T13:05:45";
        LocalDateTime expectedCreatedDate = LocalDateTime.parse(createdDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime expectedUpdatedDate = LocalDateTime.parse(createdDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // When
        TimestampsDto timeStampsDto = new TimestampsDto(createdDate, updatedDate);
        LocalDateTime actualCreatedDate = LocalDateTime.parse(timeStampsDto.createdDate());
        LocalDateTime actualUpdatedDate = LocalDateTime.parse(timeStampsDto.updatedDate());

        // Then
        Assertions.assertNotNull(actualCreatedDate);
        Assertions.assertNotNull(actualUpdatedDate);
        Assertions.assertEquals(expectedCreatedDate, actualCreatedDate);
        Assertions.assertEquals(expectedUpdatedDate, actualUpdatedDate);
    }

    @Test
    void init_DatesReceivedWithNoHour_ReturnsBeginningOfDay() {
        // Given
        String createdDate = "2015-05-25";
        String updatedDate = "2015-05-25";
        LocalDateTime expectedCreatedDate = LocalDate.parse(createdDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0);
        LocalDateTime expectedUpdatedDate = LocalDate.parse(updatedDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0);

        // When
        TimestampsDto timeStampsDto = new TimestampsDto(createdDate, updatedDate);
        LocalDateTime actualCreatedDate = LocalDateTime.parse(timeStampsDto.createdDate());
        LocalDateTime actualUpdatedDate = LocalDateTime.parse(timeStampsDto.updatedDate());

        // Then
        Assertions.assertNotNull(actualCreatedDate);
        Assertions.assertNotNull(actualUpdatedDate);
        Assertions.assertEquals(expectedCreatedDate, actualCreatedDate);
        Assertions.assertEquals(expectedUpdatedDate, actualUpdatedDate);
    }

    @Test
    void withUpdateDate_DifferentDate_ReturnTimestampsDto() {
        // Given
        String createdDate = "2002-02-02T02:02:02";
        String updatedDate = "2004-04-04T04:04:04";

        TimestampsDto expectedTimestampsDto = new TimestampsDto(createdDate, updatedDate);
        String differentDate = "2004-04-04T04:04:04";

        // When
        //TODO : check what should I do with the - @with function - TimestampsDto actualTimestampsDto = timestampsDto.withUpdatedDate(differentDateTimestamp);
        TimestampsDto actualTimestampsDto = timestampsDto.withUpdatedDate(updatedDate);


        // Then
        assertEquals(expectedTimestampsDto, actualTimestampsDto);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "TimestampsDto[createdDate=" + timestampsDto.createdDate() +
                ", updatedDate=" + timestampsDto.updatedDate() + "]";

        // When
        String actualString = timestampsDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}
