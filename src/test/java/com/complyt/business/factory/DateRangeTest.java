package com.complyt.business.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DateRangeTest {

    @Test
    void newPrevCalenderYear_DateRangeCreated_DateRangeReturned() {
        // Given
        LocalDateTime expectedFirstDayOfLastYear = LocalDate.now().with(firstDayOfYear()).minusYears(1).atStartOfDay();
        LocalDateTime expectedLastDayOfLastYear = expectedFirstDayOfLastYear.with(lastDayOfYear());

        //When + Then
        DateRange expectedDateRange = DateRange.Factory.newPrevCalenderYear();
        assertEquals(expectedDateRange.getStart(), expectedFirstDayOfLastYear);
        assertEquals(expectedDateRange.getEnd(), expectedLastDayOfLastYear);
    }

    @Test
    void newPrevAndCurrentCalenderYear_DateRangeCreated_DateRangeReturned() {
        // Given
        LocalDateTime expectedFirstDayOfLastYear = LocalDate.now().with(firstDayOfYear()).minusYears(1).atStartOfDay();
        LocalDateTime expectedNow = LocalDateTime.now();

        //When + Then
        DateRange expectedDateRange = DateRange.Factory.newPrevAndCurrentCalenderYear();
        assertEquals(expectedDateRange.getStart(), expectedFirstDayOfLastYear);
        assertEquals(expectedDateRange.getEnd().getYear(), expectedNow.getYear());
        assertEquals(expectedDateRange.getEnd().getMonthValue(), expectedNow.getMonthValue());
        assertEquals(expectedDateRange.getEnd().getDayOfMonth(), expectedNow.getDayOfMonth());
    }

    @Test
    void newPrevTwelveMonths_DateRangeCreated_DateRangeReturned() {
        // Given
        LocalDateTime expectedOneYearAgo = LocalDate.now().minusYears(1).atStartOfDay();
        LocalDateTime expectedNow = LocalDateTime.now();

        //When + Then
        DateRange expectedDateRange = DateRange.Factory.newPrevTwelveMonths();
        assertEquals(expectedDateRange.getStart(), expectedOneYearAgo);
        assertEquals(expectedDateRange.getEnd().getYear(), expectedNow.getYear());
        assertEquals(expectedDateRange.getEnd().getMonthValue(), expectedNow.getMonthValue());
        assertEquals(expectedDateRange.getEnd().getDayOfMonth(), expectedNow.getDayOfMonth());
    }

    @Test
    void newYearFromSeptember_CurrentDatePassedSeptember_DateRangeReturned() {
        // Given
        LocalDate september30 = LocalDate.now().withMonth(9).withDayOfMonth(30);
        int minusYears = LocalDate.now().compareTo(september30) > 0 ? 1 : 2;

        LocalDateTime expectedStartDate = september30.minusYears(minusYears).atStartOfDay();
        LocalDateTime expectedEndDate = september30.minusYears(minusYears-1).atStartOfDay();

        //When + Then
        DateRange expectedDateRange = DateRange.Factory.newYearFromSeptember();
        assertEquals(expectedDateRange.getStart(), expectedStartDate);
        assertEquals(expectedDateRange.getEnd(), expectedEndDate);
    }

    @Test
    void newTaxableYear_DateRangeCreated_DateRangeReturned() {
        // Given
        LocalDate taxableDate = LocalDate.now().withMonth(9).withDayOfMonth(30);
        int minusYears = LocalDate.now().compareTo(taxableDate) > 0 ? 1 : 2;

        LocalDateTime expectedStartDate = taxableDate.minusYears(minusYears).atStartOfDay();
        LocalDateTime expectedEndDate = expectedStartDate.plusYears(1).minusDays(1);

        //When + Then
        DateRange expectedDateRange = DateRange.Factory.newTaxableYear(taxableDate);
        assertEquals(expectedDateRange.getStart(), expectedStartDate);
        assertEquals(expectedDateRange.getEnd(), expectedEndDate);
    }
}
