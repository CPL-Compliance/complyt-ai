package com.complyt.v1.mappers;

import com.complyt.domain.timestamps.Timestamps;
import com.complyt.v1.error_messages.DateErrorMessages;
import com.complyt.v1.models.timestamps.TimestampsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ObjectStub;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TImestampsMapperTest {
    private Timestamps timestamps;
    private TimestampsDto timestampsDto;
    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                LocalDateTime.now(), UUID.randomUUID().toString());
        timestamps = objectStub.createTimestamps();
        timestampsDto = objectStub.createTimestampsDto();
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

    // Todo
//    @Test
//    void mapping_BlankTimeDateTimeFormat_throwsException() {
//        // Given
//        String givenDateTimeString = "";
//
//        // When
//        ParseException parseException = assertThrows(ParseException.class, () -> TimestampsMapper.INSTANCE.parseStringToLocalDateTime(givenDateTimeString));
//
//        // Then
//        assertEquals(parseException.getMessage(), "Failed on parsing string to LocalDateTime " + DateErrorMessages.wrong_format_error_message);
//    }
}