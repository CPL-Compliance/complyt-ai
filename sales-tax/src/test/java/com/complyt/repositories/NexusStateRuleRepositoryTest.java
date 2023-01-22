package com.complyt.repositories;

import com.complyt.domain.State;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.timestamps.ComplytTimestamp;
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
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class NexusStateRuleRepositoryTest {

    @InjectMocks
    NexusStateRuleRepository nexusStateRuleRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    NexusStateRule nexusStateRule;
    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        MockitoAnnotations.openMocks(this);
        nexusStateRule = domainObjectStub.createNexusStateRule(UUID.randomUUID().toString());
    }

    @Test
    void findById_FindsStateRule_ReturnsStateRule() {
        // Given
        String id = nexusStateRule.getId();

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
    void findByState_FindsRule_ReturnsRule() {
        // Given
        String state = nexusStateRule.getState().getAbbreviation();
        Criteria stateSearchCriteria = new Criteria()
                .orOperator(Criteria.where("state.abbreviation").is(state),
                        Criteria.where("state.name").is(state));
        Query query = Query.query(stateSearchCriteria);

        // When
        when(reactiveMongoTemplate.findOne(query, NexusStateRule.class)).thenReturn(Mono.just(nexusStateRule));
        Mono<NexusStateRule> actualStateRule = nexusStateRuleRepository.findByState(state);

        // Then
        StepVerifier.create(actualStateRule).expectNext(nexusStateRule).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void findByState_NullStatePassed_ThrowsException() {
        // Given
        String nullStateAbbreviation = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> nexusStateRuleRepository.findByState(nullStateAbbreviation));

        // Then
        assertEquals(nullPointerException.getMessage(), "state is marked non-null but is null");
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
