package com.complyt.repositories.security;

import com.complyt.domain.security.User;
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
public class UserRepository {
    @NonNull
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<User> findByName(String name) {
        Query query = Query.query(Criteria.where("username").is(name));

        return reactiveMongoTemplate.findOne(query, User.class);
    }

    public Mono<User> insert(User user) {
        return reactiveMongoTemplate.save(user);
    }
}