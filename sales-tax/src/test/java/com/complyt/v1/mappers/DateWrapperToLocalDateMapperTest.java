package com.complyt.v1.mappers;

import com.complyt.v1.models.nexus.DateWrapperDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DateWrapperToLocalDateMapperTest {

    @Test
    void dateWrapperToLocalDate_NullDateWrapper_ReturnsNull() {
        assertNull(DateWrapperToLocalDateMapper.INSTANCE.dateWrapperToLocalDate(null));
    }

    @Test
    void dateWrapperToLocalDate_DateWrapper_ReturnsSameDate() {
        // Given
        LocalDate localDate = LocalDate.now();
        DateWrapperDto dateWrapperDto = new DateWrapperDto(localDate.toString());

        // Then
        assertEquals(localDate, DateWrapperToLocalDateMapper.INSTANCE.dateWrapperToLocalDate(dateWrapperDto));
    }

}