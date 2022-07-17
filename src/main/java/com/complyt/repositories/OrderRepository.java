package com.complyt.repositories;

import com.complyt.domain.Customer;
import com.complyt.domain.Order;
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

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
@AllArgsConstructor
public class OrderRepository {
    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<Order> save(@NonNull Order order) {

//        ReactiveSecurityContextHolder.getContext().switchIfEmpty(this::print);
//                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal());



        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> reactiveMongoTemplate.save(order.withClientId(user.getClientId()))
                        .flatMap(savedOrder -> reactiveMongoTemplate.findById(savedOrder.getCustomerId(), Customer.class)
                            .map(savedOrder::withCustomer)));
    }
    void print(){
        System.out.println("empty");
    }

    public Flux<Order> saveAll(List<Order> orders) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .map(user -> orders.stream().map(order -> order.withClientId(user.getClientId())).collect(Collectors.toList()))
                .flatMapMany(ordersWithClientId -> reactiveMongoTemplate.insertAll(ordersWithClientId)
                        .flatMap(order -> reactiveMongoTemplate.findById(order.getCustomerId(), Customer.class)
                                .map(order::withCustomer))).log();
    }

    public Mono<Order> findById(@NonNull String orderId) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    Query query = Query.query(Criteria.where("_id").is(orderId)
                            .and("clientId").is(user.getClientId()));
                    log.debug("Searching for an order with id of : " + orderId);

                    return reactiveMongoTemplate
                            .findOne(query, Order.class)
                            .flatMap(order -> reactiveMongoTemplate
                                    .findById(order.getCustomerId(), Customer.class)
                                    .map(order::withCustomer)).log();
                });
    }

    public Mono<Order> findByExternalId(String externalId) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    Query query = Query.query(Criteria.where("externalId").is(externalId)
                            .and("clientId").is(user.getClientId()));
                    log.debug("Searching for an order with external id of : " + externalId);

                    return reactiveMongoTemplate
                            .findOne(query, Order.class)
                            .flatMap(order -> reactiveMongoTemplate
                                    .findById(order.getCustomerId(), Customer.class)
                                    .map(order::withCustomer));
                }).log();
    }

    public Flux<Order> find() {
        return ReactiveSecurityContextHolder.getContext().log()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal()).log()
                .flatMapMany(user -> {
                    Query query = Query.query(Criteria.where("clientId").is(user.getClientId()));
                    log.debug("Executing find client's related orders");

                    return reactiveMongoTemplate.find(query, Order.class)
                            .flatMap(order -> reactiveMongoTemplate.findById(order.getCustomerId(), Customer.class)
                                    .map(order::withCustomer)).log();
                });
    }

    public Flux<Order> find(Query query) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMapMany(user -> {
                    log.debug("Executing find client's related orders by query : " + query);

                    return reactiveMongoTemplate.find(query, Order.class).log()
                            .flatMap(order -> reactiveMongoTemplate.findById(order.getCustomerId(), Customer.class).log()
                                    .map(order::withCustomer));
                });
    }
}