package com.complyt.business.factory;

import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@Getter
public class DateRange {

    private final LocalDateTime start;
    private final LocalDateTime end;

    private DateRange(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public static class Factory {

        public static DateRange newPrevCalenderYear() {
            LocalDateTime firstDayOfLastYear = LocalDate.now().with(firstDayOfYear()).minusYears(1).atStartOfDay();
            LocalDateTime lastDayOfLastYear = firstDayOfLastYear.with(lastDayOfYear());
            return new DateRange(firstDayOfLastYear, lastDayOfLastYear);
        }

        public static DateRange newPrevAndCurrentCalenderYear() {
            LocalDateTime firstDayOfLastYear = LocalDate.now().with(firstDayOfYear()).minusYears(1).atStartOfDay();
            LocalDateTime now = LocalDateTime.now();
            return new DateRange(firstDayOfLastYear, now);
        }

        public static DateRange newPrevTwelveMonths() {
            LocalDateTime oneYearAgo = LocalDate.now().minusYears(1).atStartOfDay();
            LocalDateTime now = LocalDateTime.now();
            return new DateRange(oneYearAgo, now);
        }

        public static DateRange newYearFromSeptember() {
            LocalDate currentDate = LocalDate.now();
            LocalDate september30 = LocalDate.now().withMonth(9).withDayOfMonth(30);
            LocalDateTime startDate, endDate;

            // from october 1st to december 31st
            if (currentDate.compareTo(september30) > 0) {
                startDate = september30.minusYears(1).atStartOfDay();
                endDate = september30.atStartOfDay();
            } else {
                startDate = september30.minusYears(2).atStartOfDay();
                endDate = september30.minusYears(1).atStartOfDay();
            }

            return new DateRange(startDate, endDate);
        }

        public static DateRange newTaxableYear(@NonNull LocalDate taxableDate) {
            LocalDate currentDate = LocalDate.now();
            LocalDateTime startDate, endDate;
            int minusYears;

            if (currentDate.compareTo(taxableDate) > 0) {
                minusYears = 1;
            } else {
                minusYears = 2;
            }

            startDate = currentDate
                    .minusYears(minusYears)
                    .withMonth(taxableDate.getMonthValue())
                    .withDayOfMonth(taxableDate.getDayOfMonth())
                    .atStartOfDay();

            endDate = startDate
                    .plusYears(1)
                    .minusDays(1);

            return new DateRange(startDate, endDate);
        }
    }
}
