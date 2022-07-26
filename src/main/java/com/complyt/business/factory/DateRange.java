package com.complyt.business.factory;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.With;

import java.time.LocalDateTime;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@Getter
@ToString
@With
public class DateRange {

    private final LocalDateTime start;
    private final LocalDateTime end;

    private DateRange(@NonNull LocalDateTime start, @NonNull LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    /*
    Each factory method returns a date range object instance
    with start and end dates according to
    a nexus state rule timeframe.
    */
    public static class Factory {

        public static DateRange newPreviousCalenderYear(LocalDateTime referenceDate) {
            LocalDateTime firstDayOfLastYear = referenceDate
                    .with(firstDayOfYear())
                    .minusYears(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0);

            LocalDateTime lastDayOfLastYear = firstDayOfLastYear
                    .with(lastDayOfYear());

            return new DateRange(firstDayOfLastYear, lastDayOfLastYear);
        }

        public static DateRange newCurrentCalenderYear(LocalDateTime referenceDate) {
            LocalDateTime firstDayOfTheYear = referenceDate
                    .with(firstDayOfYear())
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0);

            return new DateRange(firstDayOfTheYear, referenceDate);
        }

        public static DateRange newPreviousTwelveMonths(LocalDateTime referenceDate) {
            LocalDateTime oneYearAgo = referenceDate
                    .minusYears(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0);

            return new DateRange(oneYearAgo, referenceDate);
        }

        public static DateRange newYearFromSeptember(LocalDateTime referenceDate) {
            LocalDateTime september30 = referenceDate.withMonth(9).withDayOfMonth(30);
            LocalDateTime startDate, endDate;

            // from october 1st to december 31st
            if (referenceDate.compareTo(september30) > 0) {
                startDate = september30
                        .minusYears(1)
                        .withHour(0)
                        .withMinute(0)
                        .withSecond(0);

                endDate = september30
                        .withHour(0)
                        .withMinute(0)
                        .withSecond(0);
            } else {
                startDate = september30
                        .minusYears(2)
                        .withHour(0)
                        .withMinute(0)
                        .withSecond(0);

                endDate = september30
                        .minusYears(1)
                        .withHour(0)
                        .withMinute(0)
                        .withSecond(0);
            }

            return new DateRange(startDate, endDate);
        }

        public static DateRange newTaxableYear(@NonNull LocalDateTime taxableDate, LocalDateTime referenceDate) {
            LocalDateTime startDate;
            int minusYears;

            if (referenceDate.compareTo(taxableDate) > 0) {
                minusYears = 0;
            } else {
                minusYears = 1;
            }

            startDate = referenceDate
                    .minusYears(minusYears)
                    .withMonth(taxableDate.getMonthValue())
                    .withDayOfMonth(taxableDate.getDayOfMonth())
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0);

            return new DateRange(startDate, referenceDate);
        }
    }
}
