package com.complyt.utils.query;

import com.complyt.domain.Nexus;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.utils.factory.DateRange;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class TimeFrameQueryBuilder implements QueryBuilder<DateRange> {

    @Override
    public Query build(@NonNull DateRange dateRange) {

        return Query.query(Criteria.where("externalTimestamps.createdDate")
                .gte(dateRange.getStart()).lte(dateRange.getEnd()));
    }

    public Query buildNexusTimeFrame(@NonNull Nexus nexusInfo, @NonNull NexusStateRule nexusStateRule, @NonNull LocalDateTime referenceDate) {
        TimeFrame timeFrame = nexusStateRule.timeFrame();
        LocalDateTime taxableDate = nexusInfo.getTaxableDate();

        DateRangeStrategy dateRangeStrategy = new DateRangeStrategy(timeFrame, taxableDate, referenceDate);
        DateRange dateRange = dateRangeStrategy.getDateRange();

        log.debug("Building new nexus Date range object, start date : " + dateRange.getStart() +
                  " , end date : " + dateRange.getEnd());

        return build(dateRange);
    }
}

