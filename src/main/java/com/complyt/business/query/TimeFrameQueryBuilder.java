package com.complyt.business.query;

import com.complyt.business.factory.DateRange;
import com.complyt.domain.Nexus;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.enums.TimeFrame;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TimeFrameQueryBuilder implements QueryBuilder<DateRange> {

    @Override
    public Query build(@NonNull DateRange dateRange) {

        return Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(dateRange.getStart()).lte(dateRange.getEnd()));

    }

    public Query buildDateRange(@NonNull TimeFrame timeFrame) {
        DateRange dateRange = null;

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

        }
        return build(dateRange);
    }

    public Query buildTaxableYearDateRange(@NonNull LocalDate taxableDate) {
        return build(DateRange.Factory.newTaxableYear(taxableDate));
    }

    public Query buildNexusTimeFrame(Nexus nexusInfo, NexusStateRule nexusStateRule) {
        if(nexusStateRule.getTimeFrame() == TimeFrame.CURRENT_AND_PREVIOUS_TAXABLE_YEAR)
        {
            if(nexusInfo.isHasTaxableDate())
                return buildTaxableYearDateRange(nexusInfo.getTaxableDate());
            return build(DateRange.Factory.newPrevAndCurrentCalenderYear());
        }


        return buildDateRange(nexusStateRule.getTimeFrame());
    }

}