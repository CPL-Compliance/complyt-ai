package com.complyt.business.nexus;

import com.complyt.domain.nexus.enums.TimeFrame;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ApplicationDateCreator {

    public LocalDateTime create(@NonNull TimeFrame timeFrame,@NonNull LocalDateTime referenceDate) {
        if(timeFrame.equals(TimeFrame.PREVIOUS_CALENDER_YEAR)) {
            return applyNextCalenderYear(referenceDate);
        }
        if(timeFrame.equals(TimeFrame.YEAR_FROM_SEPTEMBER_TO_SEPTEMBER)) {
            return applyNextSeptember(referenceDate);
        }

        return LocalDateTime.now().minusHours(3);
    }

    public LocalDateTime applyNextCalenderYear(@NonNull LocalDateTime referenceDate) {
        return referenceDate
                .plusYears(1)
                .with(firstDayOfYear())
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    public LocalDateTime applyNextSeptember(@NonNull LocalDateTime referenceDate) {

        LocalDateTime september30 = referenceDate
                .withMonth(9)
                .withDayOfMonth(30)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        // from october 1st to december 31st
        if(referenceDate.compareTo(september30) >= 0) {
            return september30.plusYears(1);
        }
        return september30;
    }
}
