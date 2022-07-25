package com.complyt.business.query;

import com.complyt.business.factory.DateRange;
import com.complyt.domain.Nexus;
import com.complyt.domain.State;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.enums.TimeFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TimeFrameQueryBuilderTest {

    TimeFrameQueryBuilder timeFrameQueryBuilder;
    Nexus nexusInfo;
    NexusStateRule nexusStateRule;

    @BeforeEach
    void setUp() {
        timeFrameQueryBuilder = new TimeFrameQueryBuilder();
        nexusStateRule = createNexusStateRule();
        nexusInfo = new Nexus(false, null);
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        return new NexusStateRule(UUID.randomUUID().toString(), true, state, null, null, null,
                TimeFrame.CURRENT_AND_PREVIOUS_CALENDER_YEAR, null);
    }

    @Test
    void buildNexusTimeFrame_BuildingPrevCalenderYear_ReturnsQuery() {
        // Given
        NexusStateRule ruleWithPrevCalenderYearTimeFrame = nexusStateRule.withTimeFrame(TimeFrame.PREVIOUS_CALENDER_YEAR);
        DateRange dateRange = DateRange.Factory.newPreviousCalenderYear();
        Query expectedQuery = Query.query(Criteria.where("externalTimeStamps.createdDate").gte(dateRange.getStart()).lte(dateRange.getEnd()));

        // When + Then
        Query actualQuery = timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo, ruleWithPrevCalenderYearTimeFrame);
        assertEquals(expectedQuery, actualQuery);
    }

//        @Test
//    void buildNexusTimeFrame_BuildingPrevAndCurrentCalenderYear_ReturnsQuery() {
//        // Given
//        NexusStateRule ruleWithPrevCalenderYearTimeFrame = nexusStateRule.withTimeFrame(TimeFrame.CURRENT_AND_PREVIOUS_CALENDER_YEAR);
//        DateRange dateRange = DateRange.Factory.newPreviousAndCurrentCalenderYear();
//        DateRange dateRangeWithFixedEndDate = dateRange.withEnd(dateRange.getEnd().withHour(0).withMinute(0).withSecond(0).withNano(0));
//        Query expectedQuery = Query.query(Criteria.where("externalTimeStamps.createdDate")
//                .gte(dateRange.getStart())
//                .lte(dateRangeWithFixedEndDate.getEnd()));
//
//        // When + Then
//        Query actualQuery = timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo,ruleWithPrevCalenderYearTimeFrame);
//        assertEquals(expectedQuery,actualQuery);
//    }

    @Test
    void buildNexusTimeFrame_BuildingFromSeptemberToSeptember_ReturnsQuery() {
        // Given
        NexusStateRule ruleWithSeptemberToSeptemberTimeFrame = nexusStateRule.withTimeFrame(TimeFrame.YEAR_FROM_SEPTEMBER_TO_SEPTEMBER);
        DateRange dateRange = DateRange.Factory.newYearFromSeptember();
        Query expectedQuery = Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(dateRange.getStart())
                .lte(dateRange.getEnd()));

        // When + Then
        Query actualQuery = timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo, ruleWithSeptemberToSeptemberTimeFrame);
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void buildNexusTimeFrame_BuildingTaxableYearRange_ReturnsQuery() {
        // Given
        NexusStateRule ruleWithTaxableYearTimeFrame = nexusStateRule.withTimeFrame(TimeFrame.CURRENT_AND_PREVIOUS_TAXABLE_YEAR);
        Nexus nexusWithTaxableDate = nexusInfo.withTaxableDate(LocalDate.now().with(firstDayOfYear()));
        DateRange dateRange = DateRange.Factory.newTaxableYear(nexusWithTaxableDate.getTaxableDate());
        Query expectedQuery = Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(dateRange.getStart())
                .lte(dateRange.getEnd()));

        // When + Then
        Query actualQuery = timeFrameQueryBuilder.buildNexusTimeFrame(nexusWithTaxableDate, ruleWithTaxableYearTimeFrame);
        assertEquals(expectedQuery, actualQuery);
    }
}
