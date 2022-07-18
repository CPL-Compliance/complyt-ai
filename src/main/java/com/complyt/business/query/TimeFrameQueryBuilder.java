package com.complyt.business.query;

import com.complyt.business.factory.DateRange;
import com.complyt.domain.Nexus;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.enums.TimeFrame;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

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
        DateRange dateRange;

        switch (timeFrame) {
            case PREVIOUS_CALENDER_YEAR:
                dateRange = DateRange.Factory.newPrevCalenderYear();
                break;

            case CURRENT_AND_PREVIOUS_CALENDER_YEAR:
                dateRange = DateRange.Factory.newPrevAndCurrentCalenderYear();
                break;

            case PREVIOUS_TWELVE_MONTHS:
                dateRange = DateRange.Factory.newPrevTwelveMonths();
                break;

            case YEAR_FROM_SEPTEMBER_TO_SEPTEMBER:
                dateRange = DateRange.Factory.newYearFromSeptember();
                break;

            default:
                dateRange = DateRange.Factory.newTaxableYear(nexusInfo.getTaxableDate());
        }

        log.debug("Building new nexus Date range object, start date : " + dateRange.getStart() + " , end date : " + dateRange.getEnd());
        return build(dateRange);
    }

}