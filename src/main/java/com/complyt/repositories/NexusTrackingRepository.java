package com.complyt.repositories;

import com.complyt.domain.nexus.NexusTracking;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Repository
@Slf4j
@AllArgsConstructor
public class NexusTrackingRepository {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<NexusTracking> findByState(@NonNull String state){
        Query query = Query.query(Criteria.where("state.abbreviation").is(state));
        return reactiveMongoTemplate.findOne(query, NexusTracking.class).log();
    }


}
