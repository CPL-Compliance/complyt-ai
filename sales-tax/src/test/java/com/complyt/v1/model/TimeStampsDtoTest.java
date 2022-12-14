package com.complyt.v1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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