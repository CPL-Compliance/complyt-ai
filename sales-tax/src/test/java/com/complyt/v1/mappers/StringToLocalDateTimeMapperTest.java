package com.complyt.v1.mappers;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class StringToLocalDateTimeMapperTest {
    private String dateString;
    private LocalDateTime dateLocalDateTime;
    private String exceptionMessage;

    @BeforeEach
    void setUp() {
        dateString = "2023-03-17T00:00";
        dateLocalDateTime = LocalDateTime.parse(dateString);
        exceptionMessage = "Failed on parsing string to LocalDateTime " + DtoErrorMessages.DATE_FORMAT_ERROR;
    }

    @Test
    void localDateTime_String_ReturnDateString() {
        // Given
        LocalDateTime givenLocalDateTime = dateLocalDateTime;

        // When
        String actualDateString = StringToLocalDateTimeMapper.INSTANCE.localDateTimeToString(givenLocalDateTime);

        // Then
        assertEquals(dateString, actualDateString);
    }

    @Test
    void localDateTimeNull_String_ReturnsNull() {
        // Given
        LocalDateTime givenLocalDateTime = null;

        // When
        String actualDateString = StringToLocalDateTimeMapper.INSTANCE.localDateTimeToString(givenLocalDateTime);

        // Then
        assertNull(actualDateString);
    }

    @Test
    void StringJustDate_LocalDateTime_ReturnLocalDateTime() throws ParseException {
        // Given
        String givenLocalDateTimeString = "2023-03-17";

        // When
        LocalDateTime actualLocalDateTime = StringToLocalDateTimeMapper.INSTANCE.parseStringToLocalDateTime(givenLocalDateTimeString);

        // Then
        assertEquals(dateLocalDateTime, actualLocalDateTime);
    }

    @Test
    void StringDateWithExectTime_LocalDateTime_ReturnLocalDateTime() throws ParseException {
        // Given
        String givenLocalDateTimeString = "2023-03-17T00:00";

        // When
        LocalDateTime actualLocalDateTime = StringToLocalDateTimeMapper.INSTANCE.parseStringToLocalDateTime(givenLocalDateTimeString);

        // Then
        assertEquals(dateLocalDateTime, actualLocalDateTime);
    }

    @Test
    void StringDateWithExactTimeAndOffset_LocalDateTime_ReturnLocalDateTime() throws ParseException {
        // Given
        String givenLocalDateTimeString = "2023-03-17T00:00+00:00";

        // When
        LocalDateTime actualLocalDateTime = StringToLocalDateTimeMapper.INSTANCE.parseStringToLocalDateTime(givenLocalDateTimeString);

        // Then
        assertEquals(dateLocalDateTime, actualLocalDateTime);
    }

    @Test
    void StringDateWithExactTimeAndOffsetOfZ_LocalDateTime_ReturnLocalDateTime() throws ParseException {
        // Given
        String givenLocalDateTimeString = "2023-03-17T00:00Z";

        // When
        LocalDateTime actualLocalDateTime = StringToLocalDateTimeMapper.INSTANCE.parseStringToLocalDateTime(givenLocalDateTimeString);

        // Then
        assertEquals(dateLocalDateTime, actualLocalDateTime);
    }

    @Test
    void StringDateWithWrongFormat_LocalDateTime_ThrowsParseException() throws ParseException {
        // Given
        String givenLocalDateTimeString = "23-03-17";

        // When
        Exception exception = assertThrows(ParseException.class,
                () -> StringToLocalDateTimeMapper.INSTANCE.parseStringToLocalDateTime(givenLocalDateTimeString));

        // Then
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void StringDateNull_LocalDateTime_ReturnsNull() throws ParseException {
        // Given
        String givenLocalDateTimeString = null;

        // When
        LocalDateTime actualLocalDateTime = StringToLocalDateTimeMapper.INSTANCE.parseStringToLocalDateTime(givenLocalDateTimeString);

        // Then
        assertNull(actualLocalDateTime);
    }

}