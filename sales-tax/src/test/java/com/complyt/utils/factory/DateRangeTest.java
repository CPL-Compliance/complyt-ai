package com.complyt.utils.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DateRangeTest {


    @Test
    void newPrevCalenderYear_DateRangeCreated_DateRangeReturned() {
        // Given
        LocalDateTime expectedFirstDayOfLastYear = LocalDateTime.now().with(firstDayOfYear());
        LocalDateTime expectedLastDayOfLastYear = expectedFirstDayOfLastYear.with(lastDayOfYear());
        LocalDateTime referenceDate = LocalDateTime.now();

        //When
        DateRange expectedDateRange = DateRange.Factory.newPreviousCalenderYear(referenceDate);

        // Then
        assertEquals(expectedDateRange.getStart().getYear(), expectedFirstDayOfLastYear.getYear());
        assertEquals(expectedDateRange.getStart().getMonthValue(), expectedFirstDayOfLastYear.getMonthValue());
        assertEquals(expectedDateRange.getStart().getDayOfMonth(), expectedFirstDayOfLastYear.getDayOfMonth());
        assertEquals(expectedDateRange.getEnd().getYear(), expectedLastDayOfLastYear.getYear());
        assertEquals(expectedDateRange.getEnd().getMonthValue(), expectedLastDayOfLastYear.getMonthValue());
        assertEquals(expectedDateRange.getEnd().getDayOfMonth(), expectedLastDayOfLastYear.getDayOfMonth());
    }

    @Test
    void newCurrentCalenderYear_DateRangeCreated_DateRangeReturned() {
        // Given
        LocalDateTime expectedFirstDayOfTheYear = LocalDate.now().with(firstDayOfYear()).atStartOfDay();
        LocalDateTime expectedLastDayOfThisYear = expectedFirstDayOfTheYear.with(lastDayOfYear())
                .with(LocalTime.of(23, 59, 59));
        LocalDateTime referenceDate = LocalDateTime.now();

        //When
        DateRange expectedDateRange = DateRange.Factory.newCurrentCalenderYear(referenceDate);

        // Then
        assertEquals(expectedDateRange.getStart().getYear(), expectedFirstDayOfTheYear.getYear());
        assertEquals(expectedDateRange.getStart().getMonthValue(), expectedFirstDayOfTheYear.getMonthValue());
        assertEquals(expectedDateRange.getStart().getDayOfMonth(), expectedFirstDayOfTheYear.getDayOfMonth());
        assertEquals(expectedDateRange.getEnd().getYear(), expectedLastDayOfThisYear.getYear());
        assertEquals(expectedDateRange.getEnd().getMonthValue(), expectedLastDayOfThisYear.getMonthValue());
        assertEquals(expectedDateRange.getEnd().getDayOfMonth(), expectedLastDayOfThisYear.getDayOfMonth());
    }

    @Test
    void newPrevTwelveMonths_DateRangeCreated_DateRangeReturned() {
        // Given
        LocalDateTime expectedOneYearAgo = LocalDate.now().minusYears(1).atStartOfDay();
        LocalDateTime referenceDate = LocalDateTime.now();

        // When
        DateRange expectedDateRange = DateRange.Factory.newPreviousTwelveMonths(referenceDate);

        // Then
        assertEquals(expectedDateRange.getStart().getYear(), expectedOneYearAgo.getYear());
        assertEquals(expectedDateRange.getStart().getMonthValue(), expectedOneYearAgo.getMonthValue());
        assertEquals(expectedDateRange.getStart().getDayOfMonth(), expectedOneYearAgo.getDayOfMonth());
        assertEquals(expectedDateRange.getEnd().getYear(), referenceDate.getYear());
        assertEquals(expectedDateRange.getEnd().getMonthValue(), referenceDate.getMonthValue());
        assertEquals(expectedDateRange.getEnd().getDayOfMonth(), referenceDate.getDayOfMonth());
    }

    @Test
    void newYearFromSeptember_CurrentDatePassedSeptember_DateRangeReturned() {
        // Given
        LocalDateTime september30 = LocalDateTime.now().withMonth(9).withDayOfMonth(30);
        LocalDateTime referenceDate = september30.plusDays(1);

        LocalDateTime expectedEndDate = september30.plusYears(1);

        // When
        DateRange expectedDateRange = DateRange.Factory.newYearFromSeptember(referenceDate);

        // Then
        assertEquals(expectedDateRange.getStart().getYear(), september30.getYear());
        assertEquals(expectedDateRange.getStart().getMonthValue(), september30.getMonthValue());
        assertEquals(expectedDateRange.getStart().getDayOfMonth(), september30.getDayOfMonth());
        assertEquals(expectedDateRange.getEnd().getYear(), expectedEndDate.getYear());
        assertEquals(expectedDateRange.getEnd().getMonthValue(), expectedEndDate.getMonthValue());
        assertEquals(expectedDateRange.getEnd().getDayOfMonth(), expectedEndDate.getDayOfMonth());
    }

    @Test
    void newYearFromSeptember_PriorToSeptember_DateRangeReturned() {
        // Given
        LocalDateTime september30 = LocalDateTime.now().withMonth(9).withDayOfMonth(30);
        LocalDateTime referenceDate = september30.minusDays(1);

        LocalDateTime expectedStartDate = september30.minusYears(1);
        LocalDateTime expectedEndDate = expectedStartDate.plusYears(1);

        // When
        DateRange actualDateRange = DateRange.Factory.newYearFromSeptember(referenceDate);

        // Then
        assertEquals(expectedStartDate.getYear(), actualDateRange.getStart().getYear());
        assertEquals(expectedStartDate.getMonthValue(), actualDateRange.getStart().getMonthValue());
        assertEquals(expectedStartDate.getDayOfMonth(), actualDateRange.getStart().getDayOfMonth());
        assertEquals(expectedEndDate.getYear(), actualDateRange.getEnd().getYear());
        assertEquals(expectedEndDate.getMonthValue(), actualDateRange.getEnd().getMonthValue());
        assertEquals(expectedEndDate.getDayOfMonth(), actualDateRange.getEnd().getDayOfMonth());
    }

    @Test
    void newTaxableYear_PriorToTaxableDate_DateRangeReturned() {
        // Given
        LocalDateTime taxableDate = LocalDateTime.now().withMonth(12).withDayOfMonth(25);
        LocalDateTime referenceDate = taxableDate.minusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0);
        LocalDateTime expectedStartDate = taxableDate.minusYears(1);

        //When
        DateRange actualDateRange = DateRange.Factory.newTaxableYear(taxableDate, referenceDate);

        // Then
        assertEquals(expectedStartDate.getYear(), actualDateRange.getStart().getYear());
        assertEquals(expectedStartDate.getMonthValue(), actualDateRange.getStart().getMonthValue());
        assertEquals(expectedStartDate.getDayOfMonth(), actualDateRange.getStart().getDayOfMonth());
    }

    @Test
    void newTaxableYear_AfterTaxableDate_DateRangeReturned() {
        // Given
        LocalDateTime taxableDate = LocalDateTime.now().withMonth(12).withDayOfMonth(25);
        LocalDateTime referenceDate = taxableDate.plusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0);
        LocalDateTime expectedStartDate = taxableDate.plusYears(0);

        //When
        DateRange actualDateRange = DateRange.Factory.newTaxableYear(taxableDate, referenceDate);

        // Then
        assertEquals(expectedStartDate.getYear(), actualDateRange.getStart().getYear());
        assertEquals(expectedStartDate.getMonthValue(), actualDateRange.getStart().getMonthValue());
        assertEquals(expectedStartDate.getDayOfMonth(), actualDateRange.getStart().getDayOfMonth());
    }

    @Test
    void newTaxableYear_ReferenceDateLaterThanTaxableDate_DateRangeReturned() {
        // Given
        LocalDateTime taxableDate = LocalDateTime.now().withMonth(12).withDayOfMonth(25);
        LocalDateTime referenceDate = taxableDate.plusYears(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime expectedStartDate = taxableDate.minusYears(0);

        //When
        DateRange actualDateRange = DateRange.Factory.newTaxableYear(taxableDate, referenceDate);

        // Then
        assertEquals(expectedStartDate.getYear(), actualDateRange.getStart().getYear());
        assertEquals(expectedStartDate.getMonthValue(), actualDateRange.getStart().getMonthValue());
        assertEquals(expectedStartDate.getDayOfMonth(), actualDateRange.getStart().getDayOfMonth());
        assertEquals(referenceDate, actualDateRange.getEnd());
    }

    @Test
    void newTaxableYear_NullTaxableDatePassed_ThrowsException() {
        // Given
        LocalDateTime nullTaxableDate = null;
        LocalDateTime referenceDate = LocalDateTime.now();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            DateRange.Factory.newTaxableYear(nullTaxableDate, referenceDate);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "taxableDate is marked non-null but is null");
    }

    @Test
    void newTaxableYear_NullReferenceDatePassed_ThrowsException() {
        // Given
        LocalDateTime nullReferenceDate = null;
        LocalDateTime taxableDate = LocalDateTime.now();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            DateRange.Factory.newTaxableYear(taxableDate, nullReferenceDate);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "referenceDate is marked non-null but is null");
    }

    @Test
    void newYearFromSeptember_NullReferenceDatePassed_ThrowsException() {
        // Given
        LocalDateTime nullReferenceDate = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            DateRange.Factory.newYearFromSeptember(nullReferenceDate);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "referenceDate is marked non-null but is null");
    }

    @Test
    void newPreviousTwelveMonths_NullReferenceDatePassed_ThrowsException() {
        // Given
        LocalDateTime nullReferenceDate = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            DateRange.Factory.newPreviousTwelveMonths(nullReferenceDate);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "referenceDate is marked non-null but is null");
    }

    @Test
    void newCurrentCalenderYear_NullReferenceDatePassed_ThrowsException() {
        // Given
        LocalDateTime nullReferenceDate = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            DateRange.Factory.newCurrentCalenderYear(nullReferenceDate);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "referenceDate is marked non-null but is null");
    }

    @Test
    void newPreviousCalenderYear_NullReferenceDatePassed_ThrowsException() {
        // Given
        LocalDateTime nullReferenceDate = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            DateRange.Factory.newPreviousCalenderYear(nullReferenceDate);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "referenceDate is marked non-null but is null");
    }

    @Test
    void DateRangeFactoryConstructor_DefaultConstructor_ReturnsInstance() {
        DateRange.Factory factory = new DateRange.Factory();
        assertEquals(factory.getClass(), DateRange.Factory.class);
    }

    @Test
    void toString_DateRange_ReturnsString() {
        // Given
        String expectedString = "DateRange(start=2000-01-01T00:00, end=2000-12-31T23:59:59)";
        DateRange dateRange = DateRange.Factory.newCurrentCalenderYear(LocalDateTime.of(2000, 5, 1, 1, 1));

        // When
        String actualString = dateRange.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}
