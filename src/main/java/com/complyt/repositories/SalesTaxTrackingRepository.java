package com.complyt.repositories;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.security.User;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Slf4j
@AllArgsConstructor
@Repository
public class SalesTaxTrackingRepository {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<SalesTaxTracking> findByState(@NonNull String state) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    Query query = Query.query(Criteria.where("state.abbreviation").is(state)
                            .and("clientId").is(user.getClientId()));

                    return reactiveMongoTemplate.findOne(query, SalesTaxTracking.class);
                });
    }

    public Mono<SalesTaxTracking> save(@NonNull SalesTaxTracking salesTaxTracking) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    log.debug("Saving modified nexus tracking : " + salesTaxTracking);

                    return reactiveMongoTemplate.save(salesTaxTracking).log();
                });
    }
}
