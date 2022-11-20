package com.complyt.utils.query;

import com.complyt.domain.Nexus;
import com.complyt.domain.State;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.utils.factory.DateRange;
import com.complyt.utils.query.TimeFrameQueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        nexusInfo = new Nexus( LocalDateTime.now());
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        return new NexusStateRule(UUID.randomUUID().toString(), true, state, null, null, null,
                TimeFrame.CURRENT_CALENDER_YEAR, null);
    }

    @Test
    void buildNexusTimeFrame_BuildingPrevCalenderYear_ReturnsQuery() {
        // Given
        NexusStateRule ruleWithPrevCalenderYearTimeFrame = nexusStateRule.withTimeFrame(TimeFrame.PREVIOUS_CALENDER_YEAR);
        LocalDateTime referenceDate = LocalDateTime.now();

        DateRange dateRange = DateRange.Factory.newPreviousCalenderYear(referenceDate);
        Query expectedQuery = Query.query(Criteria.where("externalTimeStamps.createdDate").gte(dateRange.getStart()).lte(dateRange.getEnd()));

        // When + Then
        Query actualQuery = timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo, ruleWithPrevCalenderYearTimeFrame, referenceDate);
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void buildNexusTimeFrame_BuildingCurrentCalenderYear_ReturnsQuery() {
        // Given
        NexusStateRule ruleWithPrevCalenderYearTimeFrame = nexusStateRule.withTimeFrame(TimeFrame.CURRENT_CALENDER_YEAR);
        LocalDateTime referenceDate = LocalDateTime.now();

        DateRange dateRange = DateRange.Factory.newCurrentCalenderYear(referenceDate);

        Query expectedQuery = Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(dateRange.getStart())
                .lte(dateRange.getEnd()));

        // When + Then
        Query actualQuery = timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo, ruleWithPrevCalenderYearTimeFrame,referenceDate);
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void buildNexusTimeFrame_BuildingFromSeptemberToSeptember_ReturnsQuery() {
        // Given
        NexusStateRule ruleWithSeptemberToSeptemberTimeFrame = nexusStateRule.withTimeFrame(TimeFrame.YEAR_FROM_SEPTEMBER_TO_SEPTEMBER);
        LocalDateTime referenceDate = LocalDateTime.now();

        DateRange dateRange = DateRange.Factory.newYearFromSeptember(referenceDate);
        Query expectedQuery = Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(dateRange.getStart())
                .lte(dateRange.getEnd()));

        // When + Then
        Query actualQuery = timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo, ruleWithSeptemberToSeptemberTimeFrame,referenceDate);
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void buildNexusTimeFrame_BuildingTaxableYearRange_ReturnsQuery() {
        // Given
        NexusStateRule ruleWithTaxableYearTimeFrame = nexusStateRule.withTimeFrame(TimeFrame.CURRENT_TAXABLE_YEAR);
        Nexus nexusWithTaxableDate = nexusInfo.withTaxableDate(LocalDateTime.now());
        LocalDateTime referenceDate = LocalDateTime.now();
        LocalDateTime localDateTimeTaxableDate = nexusWithTaxableDate.getTaxableDate();

        DateRange dateRange = DateRange.Factory.newTaxableYear(localDateTimeTaxableDate,referenceDate);
        Query expectedQuery = Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(dateRange.getStart())
                .lte(dateRange.getEnd()));

        // When + Then
        Query actualQuery = timeFrameQueryBuilder.buildNexusTimeFrame(nexusWithTaxableDate, ruleWithTaxableYearTimeFrame,referenceDate);
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void buildNexusTimeFrame_BuildingPreviousTwelveMonths_ReturnsQuery() {
        // Given
        NexusStateRule ruleWithTaxableYearTimeFrame = nexusStateRule.withTimeFrame(TimeFrame.PREVIOUS_TWELVE_MONTHS);
        Nexus nexusWithTaxableDate = nexusInfo.withTaxableDate(LocalDateTime.now());
        LocalDateTime referenceDate = LocalDateTime.now();

        DateRange dateRange = DateRange.Factory.newPreviousTwelveMonths(referenceDate);
        Query expectedQuery = Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(dateRange.getStart())
                .lte(dateRange.getEnd()));

        // When + Then
        Query actualQuery = timeFrameQueryBuilder.buildNexusTimeFrame(nexusWithTaxableDate, ruleWithTaxableYearTimeFrame,referenceDate);
        assertEquals(expectedQuery, actualQuery);
    }
}
