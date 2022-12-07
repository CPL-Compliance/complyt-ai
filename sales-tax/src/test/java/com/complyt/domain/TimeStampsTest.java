package com.complyt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeStampsTest {
    private TimeStamps timeStamps;

    @BeforeEach
    void setup() {
        timeStamps = new TimeStamps(
                LocalDateTime.of(2002, 2, 2, 2, 2, 2),
                LocalDateTime.of(2003, 3, 3, 3, 3, 3));
    }

    @Test
    void withUpdateDate_DifferentDate_ReturnTimeStamps() {
        // Given
        TimeStamps expectedTimeStamps = new TimeStamps(
                LocalDateTime.of(2002, 2, 2, 2, 2, 2),
                LocalDateTime.of(2004, 4, 4, 4, 4, 4));
        LocalDateTime differentDate = LocalDateTime.of(2004, 4, 4, 4, 4, 4);

        // When
        TimeStamps actualTimeStamps = timeStamps.withUpdatedDate(differentDate);

        // Then
        assertEquals(expectedTimeStamps, actualTimeStamps);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "TimeStamps(createdDate=" + timeStamps.getCreatedDate() +
                ", updatedDate=" + timeStamps.getUpdatedDate() + ")";

        // When
        String actualString = timeStamps.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}