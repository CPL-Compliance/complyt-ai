package com.complyt.utils.object_mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComplytObjectMapperTest {

    @Test
    void mapObject_FailsToConvert_ThrowsClassCastException() {
        // Given
        LocalDateTime time = LocalDateTime.now();

        // When
        ClassCastException classCastException = assertThrows(ClassCastException.class, () ->
                ComplytObjectMapper.mapObject(time, Object.class));

        // Then
        Assertions.assertEquals(ClassCastException.class, classCastException.getClass());
    }
}