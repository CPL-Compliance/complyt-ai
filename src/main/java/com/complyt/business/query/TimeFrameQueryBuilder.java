package com.complyt.business.query;

import com.complyt.business.factory.DateRange;
import com.complyt.domain.nexus.TimeFrame;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class TimeFrameQueryBuilder implements QueryBuilder<TimeFrame> {

    @Override
    public Query build(@NonNull TimeFrame timeFrame) {
        DateRange dateRange = getDateRange(timeFrame);

        return Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(dateRange.getStart()).lte(dateRange.getEnd()));

    }

    public DateRange getDateRange(@NonNull TimeFrame timeFrame) {
        switch (timeFrame) {
            case PREVIOUS_CALENDER_YEAR:
                return DateRange.Factory.newPrevCalenderYear();

            case CURRENT_AND_PREVIOUS_CALENDER_YEAR:
                return DateRange.Factory.newPrevAndCurrentCalenderYear();

            case PREVIOUS_TWELVE_MONTHS:
                return DateRange.Factory.newPrevTwelveMonths();

            default:
                return DateRange.Factory.newPrevTwelveMonths();

        }
    }
}
