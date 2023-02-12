package com.complyt.repositories;

import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.utils.observability.ContextLogger;
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
        return ContextLogger.observeCtx("Finding NexusStateRule by ID: " + id, log::info)
                .then(reactiveMongoTemplate.findById(id, NexusStateRule.class));
    }

    public Mono<NexusStateRule> findByState(@NonNull String state) {
        Criteria stateSearchCriteria = new Criteria()
                .orOperator(Criteria.where("state.abbreviation").is(state),
                        Criteria.where("state.name").is(state));

        Query query = Query.query(stateSearchCriteria);

        return ContextLogger.observeCtx("Finding NexusStateRule by state: " + state, log::info)
                .then(reactiveMongoTemplate.findOne(query, NexusStateRule.class));
    }

    public Mono<NexusStateRule> save(@NonNull NexusStateRule nexusStateRule) {
        return ContextLogger.observeCtx("Saving NexusStateRule: " + nexusStateRule, log::info)
                .then(reactiveMongoTemplate.save(nexusStateRule));
    }

    public Flux<NexusStateRule> findAll() {
        return ContextLogger.observeCtx("Returning all NexusStateRule documents", log::info)
                .thenMany(reactiveMongoTemplate.findAll(NexusStateRule.class));
    }
}