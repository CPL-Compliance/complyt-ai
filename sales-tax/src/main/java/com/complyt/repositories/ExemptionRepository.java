package com.complyt.repositories;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
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

@Repository
@Slf4j
@AllArgsConstructor
public class ExemptionRepository {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<Exemption> findByClientCustomerAndState(@NonNull Transaction transaction) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    Query query = Query.query(Criteria
                            .where("clientId").is(user.getClientId())
                            .and("customerId").is(transaction.getCustomerId())
                            .and("state.abbreviation").is(transaction.getShippingAddress().getState()));

                    log.debug("Searching for an exemption by query : " + query);

                    return reactiveMongoTemplate.findOne(query, Exemption.class).log();
                });
    }

    public Mono<Exemption> save(Exemption exemption) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> reactiveMongoTemplate.save(exemption.withClientId(user.getClientId()))).log();
    }

    public Mono<Exemption> findById(String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    Query query = Query.query(Criteria.where("_id").is(id)
                            .and("clientId").is(user.getClientId()));
                    log.debug("Searching for an exemption with id of : " + id);

                    return reactiveMongoTemplate.findOne(query, Exemption.class).log();
                });
    }

    public Flux<Exemption> findAll() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMapMany(user -> {
                    Query query = Query.query(Criteria.where("clientId").is(user.getClientId()));
                    log.debug("Executing findAll exemptions");

                    return reactiveMongoTemplate.find(query, Exemption.class).log();
                });
    }


}
