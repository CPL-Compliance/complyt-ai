package com.complyt.repositories;

import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@AllArgsConstructor
public class NexusStateRuleRepository {
    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<NexusStateRule> findById(@NonNull String id){
        return reactiveMongoTemplate.findById(id,NexusStateRule.class);
    }

    public Mono<NexusStateRule> findByState(@NonNull String state){
        Query query = Query.query(Criteria.where("state.abbreviation").is(state));
        return reactiveMongoTemplate.findOne(query, NexusStateRule.class);
    }

}
