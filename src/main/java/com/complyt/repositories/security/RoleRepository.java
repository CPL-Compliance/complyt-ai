package com.complyt.repositories.security;

import com.complyt.domain.security.Role;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@AllArgsConstructor
public class RoleRepository {
    @NonNull
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<Role> findById(ObjectId objectId){
        return reactiveMongoTemplate.findById(objectId, Role.class);
    }
}
