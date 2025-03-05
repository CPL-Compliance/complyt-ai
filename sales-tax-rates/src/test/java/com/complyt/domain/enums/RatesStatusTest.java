package com.complyt.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RatesStatusTest {

    @Test
    void values_enumHasCorrectValues_returnsExpected() {
        // When
        RatesStatus[] statuses = RatesStatus.values();

        // Then
        assertEquals(3, statuses.length);
        assertEquals(RatesStatus.UPDATE, statuses[0]);
        assertEquals(RatesStatus.ARCHIVE, statuses[1]);
        assertEquals(RatesStatus.NEW, statuses[2]);
    }

    @Test
    void valueOf_enumCanBeRetrievedByName_returnsExpected() {
        // When & Then
        assertEquals(RatesStatus.UPDATE, RatesStatus.valueOf("UPDATE"));
        assertEquals(RatesStatus.ARCHIVE, RatesStatus.valueOf("ARCHIVE"));
        assertEquals(RatesStatus.NEW, RatesStatus.valueOf("NEW"));
    }

    @Test
    void enumValues_areNotNull_returnsExpected() {
        // Ensure no null values exist in the enum
        for (RatesStatus status : RatesStatus.values()) {
            assertNotNull(status);
        }
    }
}
