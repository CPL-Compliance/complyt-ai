package com.complyt.repositories;

import com.complyt.domain.Customer;
import com.complyt.domain.security.User;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
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
public class CustomerRepository {
    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Flux<Customer> findByName(@NonNull String name) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMapMany(user -> {
                    Query query = Query.query(Criteria.where("name").regex("^" + name, "i")
                            .and("clientId").is(user.getClientId()));

                    return reactiveMongoTemplate.find(query, Customer.class);
                });
    }

    public Mono<Customer> findOneByName(@NonNull String name) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    Query query = Query.query(Criteria.where("name").is("^" + name)
                            .and("clientId").is(user.getClientId()));

                    return reactiveMongoTemplate.findOne(query, Customer.class);
                });
    }

    public Flux<Customer> findAll() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMapMany(user -> {
                    Query query = Query.query(Criteria.where("clientId").is(user.getClientId()));

                    return reactiveMongoTemplate.find(query, Customer.class);
                });
    }

    public Mono<Customer> save(@NonNull Customer customer) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> reactiveMongoTemplate.save(customer.withClientId(user.getClientId())));
    }

    public Mono<Customer> findById(String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    Query query = Query.query(Criteria.where("_id").is(id)
                            .and("clientId").is(user.getClientId()));

                    return reactiveMongoTemplate.findOne(query, Customer.class);
                });
    }

    public Mono<Customer> findByExternalId(String externalId) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    Query query = Query.query(Criteria.where("externalId").is(externalId)
                            .and("clientId").is(user.getClientId()));

                    return reactiveMongoTemplate.findOne(query, Customer.class);
                });
    }

    public Mono<Customer> findById(ObjectId id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    Query query = Query.query(Criteria.where("_id").is(id)
                            .and("clientId").is(user.getClientId()));

                    return reactiveMongoTemplate.findOne(query, Customer.class);
                });
    }
}