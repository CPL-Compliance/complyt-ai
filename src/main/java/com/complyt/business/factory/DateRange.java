package com.complyt.business.factory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;

@Getter
@Slf4j
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
            LocalDateTime lastDayOfLastYear = firstDayOfLastYear.plusYears(1).minusDays(1);
            return new DateRange(firstDayOfLastYear,lastDayOfLastYear);
        }

        public static DateRange newPrevAndCurrentCalenderYear() {
            LocalDateTime firstDayOfLastYear = LocalDate.now().with(firstDayOfYear()).minusYears(1).atStartOfDay();
            LocalDateTime now = LocalDate.now().atStartOfDay();
            return new DateRange(firstDayOfLastYear,now);
        }

        public static DateRange newPrevTwelveMonths() {
            LocalDateTime oneYearAgo = LocalDate.now().minusYears(1).atStartOfDay();
            LocalDateTime now = LocalDate.now().atStartOfDay();
            return new DateRange(oneYearAgo,now);
        }
    }

}
