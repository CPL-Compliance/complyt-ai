package com.complyt.repositories;

import com.complyt.domain.nexus.SalesTaxTracking;
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

    public Mono<SalesTaxTracking> findById(String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    Query query = Query.query(Criteria.where("_id").is(id)
                            .and("clientId").is(user.getClientId()));
                    log.debug("Searching for a sales tax tracking with id of : " + id);

                    return reactiveMongoTemplate.findOne(query, SalesTaxTracking.class).log();
                });
    }

    public Flux<SalesTaxTracking> findAll() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMapMany(user -> {
                    Query query = Query.query(Criteria.where("clientId").is(user.getClientId()));
                    log.debug("Executing findAll sales tax tracking");

                    return reactiveMongoTemplate.find(query, SalesTaxTracking.class).log();
                });
    }
}
