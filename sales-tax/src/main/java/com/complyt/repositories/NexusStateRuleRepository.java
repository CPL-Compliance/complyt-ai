package com.complyt.repositories;

import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.utils.query.CountryAndStateCriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Slf4j
@AllArgsConstructor
@Repository
public class NexusStateRuleRepository {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    CountryAndStateCriteriaBuilder countryQueryBuilder;

    public Mono<NexusStateRule> findById(@NonNull String id) {
        return ContextLogger.observeCtx("Searching for nexus state rule with ID " + id, log::info)
                .then(reactiveMongoTemplate.findById(id, NexusStateRule.class));
    }

    public Mono<NexusStateRule> findByCountryAndState(@NonNull String country, String state) {

        countryQueryBuilder.build(country, state);

        Criteria stateSearchCriteria = countryQueryBuilder.build(country, state);

        Query query = Query.query(stateSearchCriteria);

        String stateInfoIfStateExists = state != null ? " and in state " + state : "";

        return ContextLogger.observeCtx("Searching for nexus state rule with country " + country + stateInfoIfStateExists, log::info)
                .then(reactiveMongoTemplate.findOne(query, NexusStateRule.class));
    }

    public Mono<NexusStateRule> findMostRecentByCountryAndState(@NonNull String country, String state) {
        Criteria stateSearchCriteria = countryQueryBuilder.build(country, state);

        TypedAggregation<NexusStateRule> aggregation = newAggregation(NexusStateRule.class,
                Aggregation.match(stateSearchCriteria),
                Aggregation.sort(Sort.Direction.DESC, "appliedDate"),
                Aggregation.limit(1));


        String stateInfoIfStateExists = state != null ? " and in state " + state : "";

        return ContextLogger.observeCtx("Searching for nexus state rule with country " + country + stateInfoIfStateExists, log::info)
                .then(reactiveMongoTemplate.aggregate(aggregation, NexusStateRule.class).next());
    }

    public Mono<NexusStateRule> save(@NonNull NexusStateRule nexusStateRule) {
        return ContextLogger.observeCtx("Saving nexus state rule: " + nexusStateRule, log::info)
                .then(reactiveMongoTemplate.save(nexusStateRule));
    }

    public Flux<NexusStateRule> findAll() {
        return ContextLogger.observeCtx("Searching for all nexus state rule documents", log::info)
                .thenMany(reactiveMongoTemplate.findAll(NexusStateRule.class));
    }
}