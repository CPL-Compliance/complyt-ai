package com.complyt.business.query;

import com.complyt.domain.Nexus;
import com.complyt.domain.State;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.enums.TimeFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NexusOrdersSearchQueryBuilderTest {

    @InjectMocks
    private NexusOrdersSearchQueryBuilder nexusOrdersSearchQueryBuilder;

    @Mock
    private TimeFrameQueryBuilder timeFrameQueryBuilder;

    private NexusStateRule nexusStateRule;
    private Nexus nexus;
    private LocalDateTime dateReference;

    @BeforeEach
    void setUp() {
        nexusStateRule = createNexusStateRule();
        nexus = createNexusInfo();
        dateReference = LocalDateTime.now();
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        return new NexusStateRule(UUID.randomUUID().toString(), true, state, null, null, null,
                TimeFrame.CURRENT_CALENDER_YEAR, null);
    }

    private Nexus createNexusInfo() {
        return new Nexus(true,LocalDateTime.now());
    }

    @Test
    void buildNexusOrdersSearch_BuildsQuery_ReturnsQuery() {
        // Given
        LocalDateTime start = LocalDateTime.now().with(firstDayOfYear()).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = LocalDateTime.now().with(lastDayOfYear()).withHour(23).withMinute(59).withSecond(59).withNano(59);
        Query queryToSend = Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(start).lte(end));

        Query queryToAssert = Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(start).lte(end));
        Query expectedQuery = queryToAssert.addCriteria(Criteria.where("shippingAddress.state")
                .is(nexusStateRule.getState().getAbbreviation()));

        // When
        when(timeFrameQueryBuilder.buildNexusTimeFrame(nexus,nexusStateRule,dateReference)).thenReturn(queryToSend);
        Query actualQuery = nexusOrdersSearchQueryBuilder.buildNexusOrdersSearch(nexus,nexusStateRule,dateReference);

        // Then
        assertEquals(expectedQuery,actualQuery);
    }

}
