package com.complyt.repositories;

import com.complyt.domain.State;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class StateRepository {

    @NonNull
    ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<State> findOneByName(@NonNull String name) {
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        return reactiveMongoTemplate.findOne(query, State.class);
    }

    public State save(@NonNull State state) {
        return reactiveMongoTemplate.save(state).block();
    }

    public Mono<State> findById(String id) {
        return reactiveMongoTemplate.findById(id, State.class);
    }

    public Flux<State> findByName(String name) {
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        return reactiveMongoTemplate.find(query, State.class);
    }

    public Flux<State> findAll() {
        return reactiveMongoTemplate.findAll(State.class);
    }
}