package com.complyt.business.query;

import com.complyt.business.factory.DateRange;
import com.complyt.domain.Nexus;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.enums.TimeFrame;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class TimeFrameQueryBuilder implements QueryBuilder<DateRange> {

    @Override
    public Query build(@NonNull DateRange dateRange) {

        return Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(dateRange.getStart()).lte(dateRange.getEnd()));
    }

    public Query buildNexusTimeFrame(@NonNull Nexus nexusInfo, @NonNull NexusStateRule nexusStateRule) {
        TimeFrame timeFrame = nexusStateRule.getTimeFrame();
        LocalDate taxableDate = nexusInfo.getTaxableDate();
        DateRangeStrategy dateRangeStrategy = new DateRangeStrategy(timeFrame, taxableDate);
        DateRange dateRange = dateRangeStrategy.getDateRange();
        log.debug("Building new nexus Date range object, start date : " + dateRange.getStart() +
                " , end date : " + dateRange.getEnd());

        return build(dateRange);
    }
}

@Getter
@ToString
class DateRangeStrategy {
    private DateRange dateRange;

    public DateRangeStrategy(TimeFrame timeFrame, LocalDate taxableDate) {
        setUpDateRange(timeFrame, taxableDate);
    }

    private void setUpDateRange(TimeFrame timeFrame, LocalDate taxableDate) {
        switch (timeFrame) {
            case PREVIOUS_CALENDER_YEAR:
                dateRange = DateRange.Factory.newPreviousCalenderYear();
                break;

            case CURRENT_AND_PREVIOUS_CALENDER_YEAR:
                dateRange = DateRange.Factory.newPreviousAndCurrentCalenderYear();
                break;

            case PREVIOUS_TWELVE_MONTHS:
                dateRange = DateRange.Factory.newPreviousTwelveMonths();
                break;

            case YEAR_FROM_SEPTEMBER_TO_SEPTEMBER:
                dateRange = DateRange.Factory.newYearFromSeptember();
                break;

            case CURRENT_AND_PREVIOUS_TAXABLE_YEAR:
                dateRange = DateRange.Factory.newTaxableYear(taxableDate);
                break;

            default:
                throw new IllegalArgumentException("Illegal time frame received : " + timeFrame);
        }
    }
}