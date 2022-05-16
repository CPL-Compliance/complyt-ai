package com.complyt.repositories;

import com.complyt.domain.Order;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
        Query query = Query.query(Criteria.where("externalId").is(externalId));

        return reactiveMongoTemplate.findOne(query, Order.class);
    }

    public Flux<Order> findAll() {
        return reactiveMongoTemplate.findAll(Order.class);
    }
}