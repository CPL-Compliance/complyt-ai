package com.complyt.v1.mappers;

import com.complyt.domain.timestamps.Timestamps;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.TimestampsDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

public class TimestampsMapperTest {
    UnitTestUtilities testUtilities;
    private Timestamps timestamps;
    private TimestampsDto timestampsDto;



    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(
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