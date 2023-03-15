package com.complyt.domain;

import com.complyt.domain.timestamps.Timestamps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimestampsTest {
    private Timestamps timestamps;

    @BeforeEach
    void setup() {
        LocalDateTime createdDateTimestamp = LocalDateTime.of(2002, 2, 2, 2, 2, 2);
        LocalDateTime updatedDateTimestamp = LocalDateTime.of(2003, 3, 3, 3, 3, 3);
        timestamps = new Timestamps(createdDateTimestamp, updatedDateTimestamp);
    }

    @Test
    void withUpdateDate_DifferentDate_ReturnTimestamps() {
        // Given
        LocalDateTime createdDateTimestamp = LocalDateTime.of(2002, 2, 2, 2, 2, 2);
        LocalDateTime updatedDateTimestamp = LocalDateTime.of(2004, 4, 4, 4, 4, 4);
        Timestamps expectedTimestamps = new Timestamps(createdDateTimestamp, updatedDateTimestamp);
        LocalDateTime differentDate = LocalDateTime.of(2004, 4, 4, 4, 4, 4);

        // When
        Timestamps actualTimestamps = timestamps.withUpdatedDate(differentDate);

        // Then
        assertEquals(expectedTimestamps, actualTimestamps);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "Timestamps(createdDate=" + timestamps.getCreatedDate() +
                ", updatedDate=" + timestamps.getUpdatedDate() + ")";

        // When
        String actualString = timestamps.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}