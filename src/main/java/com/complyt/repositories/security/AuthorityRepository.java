package com.complyt.repositories.security;

import com.complyt.domain.security.Authority;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;


@Repository
@Slf4j
@AllArgsConstructor
@ToString
public class AuthorityRepository {
    @NonNull
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<Authority> findById(ObjectId objectId){
        return reactiveMongoTemplate.findById(objectId, Authority.class);
    }

    public Flux<Authority> find(Collection<ObjectId> objectIds){
        Query query = Query.query(Criteria.where("_id").in(objectIds));
        return reactiveMongoTemplate.find(query, Authority.class);
    }
}