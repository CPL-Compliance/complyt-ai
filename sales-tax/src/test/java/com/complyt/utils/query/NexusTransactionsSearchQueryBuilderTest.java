package com.complyt.utils.query;

import com.complyt.business.address.SupportedNonUsCountries;
import com.complyt.domain.Nexus;
import com.complyt.domain.State;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.security.TenantResolver;
import lombok.NonNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NexusTransactionsSearchQueryBuilderTest {

    @InjectMocks
    private NexusTransactionsSearchQueryBuilder nexusTransactionsSearchQueryBuilder;

    @Mock
    private TimeFrameQueryBuilder timeFrameQueryBuilder;

    @Mock
    private CountryAndStateCriteriaBuilder countryQueryBuilder;

    private NexusStateRule nexusStateRule;
    private Nexus nexus;
    private LocalDateTime dateReference;
    private LocalDate localDateReference;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        nexusStateRule = createNexusStateRule();
        nexus = createNexusInfo();
        localDateReference = LocalDate.now();
        dateReference = LocalDateTime.of(localDateReference, LocalTime.of(23, 59, 59));

    }

    private NexusStateRule createNexusStateRule() {
        String country = "USA";
        State state = new State("CA", "02", "California");
        return new NexusStateRule(UUID.randomUUID().toString(), true, country, state, null, null, null,
                TimeFrame.CURRENT_CALENDER_YEAR, null, LocalDateTime.now());
    }

    private Criteria nonUsaAbbreviationCriteria(@NonNull String country) {
        String searchTermCountry = SupportedNonUsCountries.nonUsaCountriesAbbreviations.get(country.toUpperCase());
        return Criteria.where("shippingAddress.country").is(searchTermCountry);
    }

    private Nexus createNexusInfo() {
        return new Nexus(LocalDateTime.now());
    }

    private Query createExpectedQuery(LocalDateTime start, LocalDateTime end) {
        Query query = (start == null) ? new Query() : new Query(Criteria.where("externalTimestamps.createdDate")
                .gte(start).lte(end));

        Criteria usaAbbreviationsCriteria = Criteria.where("shippingAddress.country").is("USA");

        Criteria stateCriteria = new Criteria().orOperator(
                Criteria.where("shippingAddress.state").is(nexusStateRule.state().getAbbreviation()),
                Criteria.where("shippingAddress.state").is(nexusStateRule.state().getName())
        );

        query.addCriteria(new Criteria().andOperator(usaAbbreviationsCriteria, stateCriteria));
        return query.addCriteria(Criteria.where("subsidiary").is(null));
    }

    private Query createExpectedQueryInANonUsa(LocalDateTime start, LocalDateTime end, NexusStateRule nexusStateRule) {
        Query query = new Query(Criteria.where("externalTimestamps.createdDate")
                .gte(start).lte(end));

        return query.addCriteria(nonUsaAbbreviationCriteria(nexusStateRule.country().toUpperCase()))
                .addCriteria(Criteria.where("subsidiary").is(null));
    }

    private Query createQueryToSend(LocalDateTime start, LocalDateTime end) {
        return new Query(Criteria.where("externalTimestamps.createdDate").gte(start).lte(end));
    }

    @Test
    void buildNexusTransactionsSearch_BuildsQueryWithRefDate_ReturnsQuery() {
        // Given
        LocalDateTime startOfYear = LocalDateTime.now().with(firstDayOfYear()).with(LocalDateTime.now().with(firstDayOfYear()).withHour(0).withMinute(0).withSecond(0).withNano(0));
        LocalDateTime endOfYear = LocalDateTime.now().with(lastDayOfYear()).with(LocalDateTime.now().with(lastDayOfYear()).withHour(23).withMinute(59).withSecond(59).withNano(59));
        Query expectedQuery = createExpectedQuery(startOfYear, endOfYear);

        when(timeFrameQueryBuilder.buildNexusTimeFrame(nexus, nexusStateRule, dateReference))
                .thenReturn(createQueryToSend(startOfYear, endOfYear));

        // When
        Query actualQuery = nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexus, nexusStateRule, localDateReference, null);

        // Then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void buildNexusTransactionsSearch_BuildsQueryNoRefDate_ReturnsQuery() {
        // Given
        LocalDate referenceDate = null;
        LocalDateTime startOfYear = null;
        LocalDateTime endOfYear = LocalDateTime.now().with(lastDayOfYear()).with(LocalDateTime.now().with(lastDayOfYear()).withHour(23).withMinute(59).withSecond(59).withNano(59));
        Query expectedQuery = createExpectedQuery(startOfYear, endOfYear);

        // When
        Query actualQuery = nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexus, nexusStateRule, referenceDate, null);

        // Then
        assertEquals(expectedQuery, actualQuery);
    }


    @Test
    void buildNexusTransactionsSearch_BuildsQueryNonUsaCountryWithRefDate_ReturnsQuery() {
        // Given
        LocalDateTime startOfYear = LocalDateTime.now().with(firstDayOfYear()).with(LocalDateTime.now().with(firstDayOfYear()).withHour(0).withMinute(0).withSecond(0).withNano(0));
        LocalDateTime endOfYear = LocalDateTime.now().with(lastDayOfYear()).with(LocalDateTime.now().with(lastDayOfYear()).withHour(23).withMinute(59).withSecond(59).withNano(59));
        NexusStateRule nexusStateRuleToSend = nexusStateRule.withCountry("Canada");
        Query expectedQuery = createExpectedQueryInANonUsa(startOfYear, endOfYear, nexusStateRuleToSend);

        when(timeFrameQueryBuilder.buildNexusTimeFrame(nexus, nexusStateRuleToSend, dateReference))
                .thenReturn(createQueryToSend(startOfYear, endOfYear));

        // When
        Query actualQuery = nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexus, nexusStateRuleToSend, localDateReference, null);

        // Then
        assertEquals(expectedQuery, actualQuery);
    }


    @Test
    void buildNexusTransactionsSearch_NexusInfoIsNull_ThrowsException() {
        // Given
        Nexus nullNexusInfo = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nullNexusInfo, nexusStateRule, localDateReference, null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "nexusInfo is marked non-null but is null");
    }

    @Test
    void buildNexusTransactionsSearch_NexusStateRuleIsNull_ThrowsException() {
        // Given
        NexusStateRule nullNexusStateRule = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexus, nullNexusStateRule, localDateReference, null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "nexusStateRule is marked non-null but is null");
    }
}
