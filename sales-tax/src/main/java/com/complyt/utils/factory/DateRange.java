package com.complyt.utils.factory;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@Slf4j
@Getter
@ToString
@With
@EqualsAndHashCode
public class DateRange {

    private final LocalDateTime start;
    private final LocalDateTime end;

    private DateRange(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    /*
    Each factory method returns a date range object instance
    with start and end dates according to
    a nexus state rule timeframe.
    */
    public static class Factory {

        public static DateRange newPreviousCalenderYear(@NonNull LocalDateTime referenceDate) {
            LocalDateTime firstDayOfLastYear = referenceDate
                    .with(firstDayOfYear())
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            LocalDateTime lastDayOfLastYear = firstDayOfLastYear.with(lastDayOfYear())
                    .with(LocalTime.of(23, 59, 59));

            return new DateRange(firstDayOfLastYear, lastDayOfLastYear);
        }

        public static DateRange newCurrentCalenderYear(@NonNull LocalDateTime referenceDate) {
            LocalDateTime firstDayOfTheYear = referenceDate
                    .with(firstDayOfYear())
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            LocalDateTime lastDayOfThisYear = firstDayOfTheYear.with(lastDayOfYear())
                    .with(LocalTime.of(23, 59, 59));

            return new DateRange(firstDayOfTheYear, lastDayOfThisYear);
        }

        public static DateRange newPreviousTwelveMonths(@NonNull LocalDateTime referenceDate) {
            LocalDateTime oneYearAgo = referenceDate
                    .minusYears(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            return new DateRange(oneYearAgo, referenceDate);
        }

        public static DateRange newYearFromSeptember(@NonNull LocalDateTime referenceDate) {
            LocalDateTime firstOfOctober = referenceDate
                    .withMonth(10)
                    .withDayOfMonth(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            LocalDateTime startDate, endDate;

            // from october 1st to december 31st
            startDate = referenceDate.isBefore(firstOfOctober) ? firstOfOctober.minusYears(1) : firstOfOctober;

            endDate = startDate.plusYears(1);
            return new DateRange(startDate, endDate);
        }

        public static DateRange newTaxableYear(@NonNull LocalDateTime taxableDate, @NonNull LocalDateTime referenceDate) {
            LocalDateTime startDate, endDate;
            LocalDateTime taxableDateWithSameYearAsReferenceDate = taxableDate.withYear(referenceDate.getYear());
            int minusYears;

            minusYears = referenceDate.isAfter(taxableDateWithSameYearAsReferenceDate) ? 0 : 1;

            startDate = referenceDate
                    .minusYears(minusYears)
                    .withMonth(taxableDate.getMonthValue())
                    .withDayOfMonth(taxableDate.getDayOfMonth())
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);
            endDate = startDate.plusYears(1);

            return new DateRange(startDate, endDate);
        }
    }
}
