package com.complyt.repositories;


import com.complyt.domain.Nexus;
import com.complyt.domain.State;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StateRepositoryTest {

    @InjectMocks
    private StateRepository stateRepository;

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private State state;

    @BeforeAll
    public void setUp() {
        String id = UUID.randomUUID().toString();
        double salesTaxRate = 0.6;
        String abbreviation = "CA";
        String code = "08";
        String name = "California";
        List<Nexus> nexuses = null;

        state = new State(id, salesTaxRate, abbreviation, code, name, nexuses);
    }

    @Test
    public void findByName_NameIsCalifornia_ReturnsCaliforniaState() {
        // Given
        String name = "California";
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        // When
        when(reactiveMongoTemplate.findOne(query, State.class)).thenReturn(Mono.just(state));
        Mono<State> monoState = stateRepository.findOneByName(name);

        // Then
        StepVerifier.create(monoState).expectNext(state).verifyComplete();
    }

    @Test
    public void findByName_NameIscalifornia_ReturnsCaliforniaState() {
        // Given
        String name = "california";
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        // When
        when(reactiveMongoTemplate.findOne(query, State.class)).thenReturn(Mono.just(state));
        Mono<State> monoState = stateRepository.findOneByName(name);

        // Then
        StepVerifier.create(monoState).expectNext(state).verifyComplete();
    }

    @Test
    public void findByName_NameDoesntExistInDB_ExpectsEmpty() {
        // Given
        String name = "Stam";
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        // When
        when(reactiveMongoTemplate.findOne(query, State.class)).thenReturn(Mono.empty());
        Mono<State> monoState = stateRepository.findOneByName(name);

        // Then
        StepVerifier.create(monoState).expectNextCount(0).verifyComplete();
    }
}