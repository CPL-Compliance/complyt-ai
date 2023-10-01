package com.complyt.utils.query;

import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.utils.factory.DateRange;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class DateRangeStrategy {
    private DateRange dateRange;

    public DateRangeStrategy(TimeFrame timeFrame, LocalDateTime taxableDate, LocalDateTime referenceDate) {
        setUpDateRange(timeFrame, taxableDate, referenceDate);
    }

    private void setUpDateRange(TimeFrame timeFrame, LocalDateTime taxableDate, LocalDateTime referenceDate) {
        switch (timeFrame) {
            case PREVIOUS_CALENDER_YEAR -> dateRange = DateRange.Factory.newPreviousCalenderYear(referenceDate);
            case CURRENT_CALENDER_YEAR -> dateRange = DateRange.Factory.newCurrentCalenderYear(referenceDate);
            case PREVIOUS_TWELVE_MONTHS -> dateRange = DateRange.Factory.newPreviousTwelveMonths(referenceDate);
            case YEAR_FROM_SEPTEMBER_TO_SEPTEMBER -> dateRange = DateRange.Factory.newYearFromSeptember(referenceDate);


            //CURRENT_TAXABLE_YEAR
            default -> dateRange = DateRange.Factory.newTaxableYear(taxableDate, referenceDate);
        }
    }
}
