package com.complyt.business.query;

import com.complyt.business.factory.DateRange;
import com.complyt.domain.nexus.TimeFrame;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;

@Component
public class TimeFrameQueryBuilder implements QueryBuilder<TimeFrame> {

    private final Map<TimeFrame,Query> commands = new HashMap<TimeFrame,Query>(){{
        put(TimeFrame.CURRENT_AND_PREVIOUS_CALENDER_YEAR, Query.query(Criteria.where("externalTimeStamps.createdDate").lt("asd")));
        put(TimeFrame.CURRENT_AND_PREVIOUS_TAXABLE_YEAR,Query.query(Criteria.where("externalTimeStamps.createdDate").lt("asd")));
        put(TimeFrame.PREVIOUS_TWELVE_MONTHS,Query.query(Criteria.where("externalTimeStamps.createdDate").lt("asd")));
        put(TimeFrame.PREVIOUS_CALENDER_YEAR,Query.query(Criteria.where("externalTimeStamps.createdDate").lt("asd")));
        put(TimeFrame.YEAR_FROM_SEPTEMBER_TO_SEPTEMBER,Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte("2022-06-10").lte("2022-06-16")));
    }};

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
