package com.complyt.utils.query;

import com.complyt.business.address.SupportedNonUsCountries;
import com.complyt.business.address.UsaAbbreviations;
import com.complyt.domain.Nexus;
import com.complyt.domain.State;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.enums.TimeFrame;
import lombok.NonNull;
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
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @BeforeEach
    void setUp() {
        nexusStateRule = createNexusStateRule();
        nexus = createNexusInfo();
        dateReference = LocalDateTime.now();
    }

    private NexusStateRule createNexusStateRule() {
        String country = "USA";
        State state = new State("CA", "02", "California");
        return new NexusStateRule(UUID.randomUUID().toString(), true, country, state, null, null, null,
                TimeFrame.CURRENT_CALENDER_YEAR, null, LocalDateTime.now());
    }

    private List<Criteria> listOfNonUsaAbbreviationCriteria(@NonNull String country) {
        return SupportedNonUsCountries.nonUsaCountriesAbbreviations.get(country.toUpperCase()).stream()
                .map(name -> Criteria.where("shippingAddress.country").is(name.toUpperCase())).collect(Collectors.toList());
    }

    private Nexus createNexusInfo() {
        return new Nexus(LocalDateTime.now());
    }

    private Query createExpectedQuery(LocalDateTime start, LocalDateTime end) {
        Query query = new Query(Criteria.where("externalTimestamps.createdDate")
                .gte(start).lte(end));
        Criteria usaAbbreviationsCriteria = new Criteria().orOperator(
                UsaAbbreviations.usaAbbreviationsList.stream()
                        .map(abbreviation -> Criteria.where("shippingAddress.country").is(abbreviation.toUpperCase())).collect(Collectors.toList()));

        Criteria stateCriteria = new Criteria().orOperator(
                Criteria.where("shippingAddress.state").is(nexusStateRule.state().getAbbreviation()),
                Criteria.where("shippingAddress.state").is(nexusStateRule.state().getName())
        );

        return query.addCriteria(new Criteria().andOperator(usaAbbreviationsCriteria, stateCriteria));
    }

    private Query createExpectedQueryInANonUsa(LocalDateTime start, LocalDateTime end, NexusStateRule nexusStateRule) {
        Query query = new Query(Criteria.where("externalTimestamps.createdDate")
                .gte(start).lte(end));

        return query.addCriteria(new Criteria().orOperator(listOfNonUsaAbbreviationCriteria(nexusStateRule.country().toUpperCase())));
    }

    private Query createQueryToSend(LocalDateTime start, LocalDateTime end) {
        return new Query(Criteria.where("externalTimestamps.createdDate").gte(start).lte(end));
    }

    @Test
    void buildNexusTransactionsSearch_BuildsQuery_ReturnsQuery() {
        // Given
        LocalDateTime startOfYear = LocalDateTime.now().with(firstDayOfYear()).with(LocalDateTime.now().with(firstDayOfYear()).withHour(0).withMinute(0).withSecond(0).withNano(0));
        LocalDateTime endOfYear = LocalDateTime.now().with(lastDayOfYear()).with(LocalDateTime.now().with(lastDayOfYear()).withHour(23).withMinute(59).withSecond(59).withNano(59));
        Query expectedQuery = createExpectedQuery(startOfYear, endOfYear);

        when(timeFrameQueryBuilder.buildNexusTimeFrame(nexus, nexusStateRule, dateReference))
                .thenReturn(createQueryToSend(startOfYear, endOfYear));

        // When
        Query actualQuery = nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexus, nexusStateRule, dateReference);

        // Then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void buildNexusTransactionsSearch_BuildsQueryNonUsaCountry_ReturnsQuery() {
        // Given
        LocalDateTime startOfYear = LocalDateTime.now().with(firstDayOfYear()).with(LocalDateTime.now().with(firstDayOfYear()).withHour(0).withMinute(0).withSecond(0).withNano(0));
        LocalDateTime endOfYear = LocalDateTime.now().with(lastDayOfYear()).with(LocalDateTime.now().with(lastDayOfYear()).withHour(23).withMinute(59).withSecond(59).withNano(59));
        NexusStateRule nexusStateRuleToSend = nexusStateRule.withCountry("Canada");
        Query expectedQuery = createExpectedQueryInANonUsa(startOfYear, endOfYear, nexusStateRuleToSend);

        when(timeFrameQueryBuilder.buildNexusTimeFrame(nexus, nexusStateRuleToSend, dateReference))
                .thenReturn(createQueryToSend(startOfYear, endOfYear));

        // When
        Query actualQuery = nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexus, nexusStateRuleToSend, dateReference);

        // Then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void buildNexusTransactionsSearch_NexusInfoIsNull_ThrowsException() {
        // Given
        Nexus nullNexusInfo = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nullNexusInfo, nexusStateRule, dateReference);
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
            nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexus, nullNexusStateRule, dateReference);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "nexusStateRule is marked non-null but is null");
    }

    @Test
    void buildNexusTransactionsSearch_ReferenceDateIsNull_ThrowsException() {
        // Given
        LocalDateTime nullLocalDateTime = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexus, nexusStateRule, nullLocalDateTime);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "referenceDate is marked non-null but is null");
    }
}
