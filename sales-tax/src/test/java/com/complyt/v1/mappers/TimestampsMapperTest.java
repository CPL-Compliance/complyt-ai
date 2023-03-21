package com.complyt.v1.mappers;

import com.complyt.domain.timestamps.Timestamps;
import com.complyt.v1.models.timestamps.TimestampsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ut.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimestampsMapperTest {
    TestUtilities testUtilities;
    private Timestamps timestamps;
    private TimestampsDto timestampsDto;

    @BeforeEach
    void setup() {
        testUtilities = new TestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
        timestamps = testUtilities.createTimestamps();
        timestampsDto = testUtilities.createTimestampsDto();
    }

    @Test
    void timestampToTimestampsDto_Timestamps_returnTimestampsDto() {
        // Given
        Timestamps givenTimestamps = timestamps;

        // When
        TimestampsDto actualTimestampsDto = TimestampsMapper.INSTANCE.timestampsTotimestampsDto(givenTimestamps);

        // Then
        assertEquals(timestampsDto, actualTimestampsDto);
    }

    @Test
    void timestampDtoToTimestamps_TimestampsDto_returnTimestamps() {
        // Given
        TimestampsDto givenTimestampsDto = timestampsDto;

        // When
        Timestamps actualTimestamps = TimestampsMapper.INSTANCE.timestampsDtoTotimestamps(givenTimestampsDto);

        // Then
        assertEquals(timestamps, actualTimestamps);
    }

}