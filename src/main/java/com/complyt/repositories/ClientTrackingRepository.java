package com.complyt.repositories;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.security.User;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Repository
public class ClientTrackingRepository {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<ClientTracking> findClient() {
        return ReactiveSecurityContextHolder.getContext().log()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal()).log()
                .flatMap(user -> {
                    Query query = Query.query(Criteria.where("clientId").is(user.getClientId()));

                    log.debug("Searching for a client with id of : " + user.getClientId());
                    return reactiveMongoTemplate.findOne(query,ClientTracking.class).log();
                });

    }
}
