package com.complyt.v1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeStampsDtoTest {

    private TimeStampsDto timeStampsDto;

    @BeforeEach
    void setup() {
        timeStampsDto = new TimeStampsDto(
                LocalDateTime.of(2002, 2, 2, 2, 2, 2),
                LocalDateTime.of(2003, 3, 3, 3, 3, 3));
    }

    @Test
    void withUpdateDate_DifferentDate_ReturnTimeStampsDto() {
        // Given
        TimeStampsDto expectedTimeStampsDto = new TimeStampsDto(
                LocalDateTime.of(2002, 2, 2, 2, 2, 2),
                LocalDateTime.of(2004, 4, 4, 4, 4, 4));
        LocalDateTime differentDate = LocalDateTime.of(2004, 4, 4, 4, 4, 4);

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