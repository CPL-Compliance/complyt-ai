package com.complyt.repositories;

import com.complyt.domain.State;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.utils.query.CountryAndStateCriteriaBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class NexusStateRuleRepositoryTest {

    @InjectMocks
    NexusStateRuleRepository nexusStateRuleRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Mock
    CountryAndStateCriteriaBuilder countryQueryBuilder;

    NexusStateRule nexusStateRule;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        MockitoAnnotations.openMocks(this);
        nexusStateRule = testUtilities.createNexusStateRule(UUID.randomUUID().toString());
    }

    @Test
    void findById_FindsStateRule_ReturnsStateRule() {
        // Given
        String id = nexusStateRule.id();

        // When
        when(reactiveMongoTemplate.findById(id, NexusStateRule.class)).thenReturn(Mono.just(nexusStateRule));
        Mono<NexusStateRule> actualStateRule = nexusStateRuleRepository.findById(id);

        // Then
        StepVerifier.create(actualStateRule).expectNext(nexusStateRule).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void findById_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> nexusStateRuleRepository.findById(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @Test
    void findByCountryAndState_FindsRule_ReturnsRule() {
        // Given
        String country = nexusStateRule.country();
        String state = nexusStateRule.state().getAbbreviation();
        Criteria criteria = new Criteria().orOperator(Criteria.where("state.abbreviation").is(state),
                        Criteria.where("state.name").is(state))
                .andOperator(Criteria.where("country").is("USA"));
        Query query = Query.query(criteria);

        // When
        when(countryQueryBuilder.build(country, state)).thenReturn(criteria);
        when(reactiveMongoTemplate.findOne(query, NexusStateRule.class)).thenReturn(Mono.just(nexusStateRule));
        Mono<NexusStateRule> actualStateRule = nexusStateRuleRepository.findByCountryAndState(country, state);

        // Then
        StepVerifier.create(actualStateRule).expectNext(nexusStateRule).verifyComplete();
    }

    @Test
    void findByCountryAndState_FindsRuleWithNullState_ReturnsRule() {
        // Given
        String country = nexusStateRule.country();
        String state = null;
        Criteria criteria = new Criteria().orOperator(Criteria.where("state.abbreviation").is(state),
                        Criteria.where("state.name").is(state))
                .andOperator(Criteria.where("country").is("USA"));
        Query query = Query.query(criteria);

        // When
        when(countryQueryBuilder.build(country, state)).thenReturn(criteria);
        when(reactiveMongoTemplate.findOne(query, NexusStateRule.class)).thenReturn(Mono.just(nexusStateRule));
        Mono<NexusStateRule> actualStateRule = nexusStateRuleRepository.findByCountryAndState(country, state);

        // Then
        StepVerifier.create(actualStateRule).expectNext(nexusStateRule).verifyComplete();
    }

    @Test
    void findMostRecentByState_Finds2RuleWithNullState_ReturnsFirstRule() {
        // Given
        NexusStateRule mostRecentNexusStateRule = nexusStateRule.withAppliedDate(LocalDateTime.now()).withEnforcesSalesTax(false);
        String country = nexusStateRule.country();
        String state = null;
        Criteria criteria = new Criteria().orOperator(Criteria.where("state.abbreviation").is(nexusStateRule.state().getAbbreviation()),
                Criteria.where("state.name").is(nexusStateRule.state().getName()));

        // When
        when(reactiveMongoTemplate.aggregate(any(), eq(NexusStateRule.class))).thenReturn(Flux.just(mostRecentNexusStateRule, nexusStateRule));
        when(countryQueryBuilder.build(country, state)).thenReturn(criteria);
        Mono<NexusStateRule> actualStateRule = nexusStateRuleRepository.findMostRecentByCountryAndState(country, state);

        // Then
        StepVerifier.create(actualStateRule).expectNext(mostRecentNexusStateRule).verifyComplete();
    }

    @Test
    void findMostRecentByState_Finds2Rule_ReturnsFirstRule() {
        // Given
        NexusStateRule mostRecentNexusStateRule = nexusStateRule.withAppliedDate(LocalDateTime.now()).withEnforcesSalesTax(false);
        String country = nexusStateRule.country();
        String state = nexusStateRule.state().getAbbreviation();
        Criteria criteria = new Criteria().orOperator(Criteria.where("state.abbreviation").is(nexusStateRule.state().getAbbreviation()),
                Criteria.where("state.name").is(nexusStateRule.state().getName()));

        // When
        when(reactiveMongoTemplate.aggregate(any(), eq(NexusStateRule.class))).thenReturn(Flux.just(mostRecentNexusStateRule, nexusStateRule));
        when(countryQueryBuilder.build(country, state)).thenReturn(criteria);
        Mono<NexusStateRule> actualStateRule = nexusStateRuleRepository.findMostRecentByCountryAndState(country, state);

        // Then
        StepVerifier.create(actualStateRule).expectNext(mostRecentNexusStateRule).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void findByCountryANdState_NullCountryPassed_ThrowsException() {
        // Given
        String nullCountry = null;
        String state = "";

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> nexusStateRuleRepository.findByCountryAndState(nullCountry, state));

        // Then
        assertEquals(nullPointerException.getMessage(), "country is marked non-null but is null");
    }

    @Test
    void findMostRecentByCountryAndState_NullCountryPassed_ThrowsException() {
        // Given
        String nullCountry = null;
        String state = "";

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> nexusStateRuleRepository.findMostRecentByCountryAndState(nullCountry, state));

        // Then
        assertEquals(nullPointerException.getMessage(), "country is marked non-null but is null");
    }

    @Test
    void save_SavesStateRule_ReturnsStateRule() {
        // Given

        // When
        when(reactiveMongoTemplate.save(nexusStateRule)).thenReturn(Mono.just(nexusStateRule));
        Mono<NexusStateRule> actualStateRule = nexusStateRuleRepository.save(nexusStateRule);

        // Then
        StepVerifier.create(actualStateRule).expectNext(nexusStateRule).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void save_NullStateRule_ThrowsException() {
        // Given
        NexusStateRule nullNexusStateRule = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> nexusStateRuleRepository.save(nullNexusStateRule));

        // Then
        assertEquals(nullPointerException.getMessage(), "nexusStateRule is marked non-null but is null");
    }

    @Test
    void findAll_FindsTwoStateRules_ReturnsTwoStateRules() {
        // Given
        State secondState = new State("NY", "04", "New-York");
        NexusStateRule secondStateRule = nexusStateRule.withState(secondState);
        List<NexusStateRule> nexusStateRules = new ArrayList<>() {{
            add(nexusStateRule);
            add(secondStateRule);
        }};

        // When
        when(reactiveMongoTemplate.findAll(NexusStateRule.class)).thenReturn(Flux.fromIterable(nexusStateRules));
        Flux<NexusStateRule> nexusStateRuleFlux = nexusStateRuleRepository.findAll();

        // Then
        StepVerifier.create(nexusStateRuleFlux).expectNext(nexusStateRule).expectNext(secondStateRule).verifyComplete();
    }

}
