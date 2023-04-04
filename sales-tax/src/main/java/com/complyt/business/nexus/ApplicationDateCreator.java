package com.complyt.business.nexus;

import com.complyt.domain.nexus.enums.TimeFrame;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;

@Component
@Slf4j
public class ApplicationDateCreator {

    public LocalDateTime create(@NonNull TimeFrame timeFrame, @NonNull LocalDateTime referenceDate) {
        if (timeFrame.equals(TimeFrame.PREVIOUS_CALENDER_YEAR)) {
            return applyNextCalenderYear(referenceDate);
        }
        if (timeFrame.equals(TimeFrame.YEAR_FROM_SEPTEMBER_TO_SEPTEMBER)) {
            return applyNextSeptember(referenceDate);
        }
        log.info("Creating sales tax application date : " + referenceDate);

        return referenceDate;
    }

    private LocalDateTime applyNextCalenderYear(LocalDateTime referenceDate) {
        LocalDateTime applicationDate = referenceDate
                .plusYears(1)
                .with(firstDayOfYear())
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        log.info("Creating sales tax application date : " + applicationDate);

        return applicationDate;
    }

    private LocalDateTime applyNextSeptember(LocalDateTime referenceDate) {

        LocalDateTime firstOfOctober = referenceDate
                .withMonth(10)
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime applicationDate;

        // from october 1st to december 31st
        if (referenceDate.compareTo(firstOfOctober) >= 0) {
            applicationDate = firstOfOctober.plusYears(1);
        } else {
            applicationDate = firstOfOctober;
        }
        log.info("Creating sales tax application date : " + applicationDate);

        return applicationDate;
    }
}
