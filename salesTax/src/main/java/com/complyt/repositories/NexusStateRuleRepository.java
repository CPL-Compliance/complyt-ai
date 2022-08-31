package com.complyt.repositories;

import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.security.User;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Repository
public class NexusStateRuleRepository {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<NexusStateRule> findById(@NonNull String id){
        return reactiveMongoTemplate.findById(id,NexusStateRule.class);
    }

    public Mono<NexusStateRule> findByState(@NonNull String state){
        Query query = Query.query(Criteria.where("state.abbreviation").is(state));
        return reactiveMongoTemplate.findOne(query, NexusStateRule.class).log();
    }

    public Mono<NexusStateRule> save(@NonNull NexusStateRule nexusStateRule) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> reactiveMongoTemplate.save(nexusStateRule).log());
    }

    public Flux<NexusStateRule> findAll() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMapMany(user -> {

                    log.debug("Executing findAll nexus state rule");

                    return reactiveMongoTemplate.findAll(NexusStateRule.class).log();
                });
    }

}
