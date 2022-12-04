package com.complyt.domain.nexus.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeFrameTest {
    @Test
    public void TimeFrame_GetCurrent_calender_year_ReturnCurrent_calender_year() {
        // Given + When
        TimeFrame timeFrame = TimeFrame.CURRENT_CALENDER_YEAR;

        // Then
        assertEquals(TimeFrame.valueOf("CURRENT_CALENDER_YEAR"), timeFrame);
    }

    @Test
    public void TimeFrame_GetPrevious_twelve_months_ReturnPrevious_twelve_months() {
        // Given + When
        TimeFrame timeFrame = TimeFrame.PREVIOUS_TWELVE_MONTHS;

        // Then
        assertEquals(TimeFrame.valueOf("PREVIOUS_TWELVE_MONTHS"), timeFrame);
    }

    @Test
    public void TimeFrame_GetCurrent_taxable_year_ReturnCurrent_taxable_year() {
        // Given + When
        TimeFrame timeFrame = TimeFrame.CURRENT_TAXABLE_YEAR;

        // Then
        assertEquals(TimeFrame.valueOf("CURRENT_TAXABLE_YEAR"), timeFrame);
    }

    @Test
    public void TimeFrame_GetPrevious_calender_year_ReturnPrevious_calender_year() {
        // Given + When
        TimeFrame timeFrame = TimeFrame.PREVIOUS_CALENDER_YEAR;

        // Then
        assertEquals(TimeFrame.valueOf("PREVIOUS_CALENDER_YEAR"), timeFrame);
    }

    @Test
    public void TimeFrame_GetYear_from_september_to_september_ReturnYear_from_september_to_september() {
        // Given + When
        TimeFrame timeFrame = TimeFrame.YEAR_FROM_SEPTEMBER_TO_SEPTEMBER;

        // Then
        assertEquals(TimeFrame.valueOf("YEAR_FROM_SEPTEMBER_TO_SEPTEMBER"), timeFrame);
    }

}