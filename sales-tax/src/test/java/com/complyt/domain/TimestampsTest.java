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
        timestamps = new Timestamps(
                LocalDateTime.of(2002, 2, 2, 2, 2, 2),
                LocalDateTime.of(2003, 3, 3, 3, 3, 3));
    }

    @Test
    void withUpdateDate_DifferentDate_ReturnTimestamps() {
        // Given
        Timestamps expectedTimestamps = new Timestamps(
                LocalDateTime.of(2002, 2, 2, 2, 2, 2),
                LocalDateTime.of(2004, 4, 4, 4, 4, 4));
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