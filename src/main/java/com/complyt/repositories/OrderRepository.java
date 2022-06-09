package com.complyt.repositories;

import com.complyt.domain.Customer;
import com.complyt.domain.Order;
import com.complyt.domain.security.User;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@Slf4j
@AllArgsConstructor
public class OrderRepository {
    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<Order> save(@NonNull Order order) {
        return reactiveMongoTemplate.save(order);
    }

    public Flux<Order> saveAll(List<Order> orders) {
        return reactiveMongoTemplate.insertAll(orders);
    }

    public Mono<Order> findById(@NonNull String orderId) {
        Query query = Query.query(Criteria.where("_id").is(orderId));

        return reactiveMongoTemplate.findOne(query, Order.class);
    }

    public Mono<Order> findByExternalId(String externalId) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(securityContext -> (User) securityContext
                        .getAuthentication()
                        .getPrincipal())
                .flatMap(user -> {
                    Query query = Query.query(Criteria.where("externalId").is(externalId)
                            .and("clientId").is(user.getClientId()));

                    return reactiveMongoTemplate
                            .findOne(query, Order.class).log()
                            .flatMap(order -> reactiveMongoTemplate
                                    .findById(order.getCustomerId(), Customer.class)
                                    .map(order::withCustomer).log());
                });
    }

    public Flux<Order> find() {
        return reactiveMongoTemplate
                .findAll(Order.class).log()
                .flatMap(order -> reactiveMongoTemplate
                        .findById(order.getCustomerId(), Customer.class)
                        .map(order::withCustomer).log());
    }

    public Flux<Order> find(ObjectId clientId) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Query query = Query.query(Criteria.where("clientId").is(clientId)
                .and("clientId").is(principal.getClientId()));

        return reactiveMongoTemplate.find(query, Order.class);
    }
}