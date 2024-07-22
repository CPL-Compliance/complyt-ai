package com.complyt.v1.mappers;

import com.complyt.v1.models.nexus.DateWrapperDto;
import com.complyt.v1.models.nexus.LocalDateWrapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.spi.LocaleNameProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DateWrapperToLocalDateMapperTest {

    @Test
    void dateWrapperToLocalDate_NullDateWrapper_ReturnsNull() {
        assertEquals(DateWrapperToLocalDateMapper.INSTANCE.dateWrapperToLocalDateWrapper(null), new LocalDateWrapper(null));
    }

    @Test
    void dateWrapperToLocalDate_DateWrapper_ReturnsSameDate() {
        // Given
        LocalDate localDate = LocalDate.now();
        LocalDateWrapper localDateWrapper = new LocalDateWrapper(localDate);
        DateWrapperDto dateWrapperDto = new DateWrapperDto(localDate.toString());

        // Then
        assertEquals(localDateWrapper, DateWrapperToLocalDateMapper.INSTANCE.dateWrapperToLocalDateWrapper(dateWrapperDto));
    }

    @Test
    void dateWrapperToLocalDateWrapper_NullDateWrapperDto_ReturnsNullLocalDateWrapper() {
        // Given
        DateWrapperDto dateWrapperDto = null;

        // When
        LocalDateWrapper result = DateWrapperToLocalDateMapper.INSTANCE.dateWrapperToLocalDateWrapper(dateWrapperDto);

        // Then
        assertEquals(new LocalDateWrapper(null), result);
    }

    @Test
    void dateWrapperToLocalDateWrapper_NullDateInDateWrapperDto_ReturnsNullLocalDateWrapper() {
        // Given
        DateWrapperDto dateWrapperDto = new DateWrapperDto(null);

        // When
        LocalDateWrapper result = DateWrapperToLocalDateMapper.INSTANCE.dateWrapperToLocalDateWrapper(dateWrapperDto);

        // Then
        assertEquals(new LocalDateWrapper(null), result);
    }


}