package com.complyt.repositories;

import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Repository
public class NexusStateRuleRepository {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<NexusStateRule> findById(@NonNull String id) {
        return reactiveMongoTemplate.findById(id, NexusStateRule.class).log();
    }

    public Mono<NexusStateRule> findByState(@NonNull String state) {
        Criteria stateSearchCriteria = new Criteria()
                .orOperator(Criteria.where("state.abbreviation").is(state),
                        Criteria.where("state.name").is(state));

        Query query = Query.query(stateSearchCriteria);

        return reactiveMongoTemplate.findOne(query, NexusStateRule.class).log();
    }

    public Mono<NexusStateRule> save(@NonNull NexusStateRule nexusStateRule) {
        return reactiveMongoTemplate.save(nexusStateRule).log();
    }

    public Flux<NexusStateRule> findAll() {
        log.debug("Executing findAll nexus state rule");

        return reactiveMongoTemplate.findAll(NexusStateRule.class).log();
    }
}