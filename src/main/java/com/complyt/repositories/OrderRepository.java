package com.complyt.repositories;

import com.complyt.domain.Order;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class OrderRepository {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    public Order save(@NonNull Order order) {
        return reactiveMongoTemplate.save(order).block();
    }

    public void insertAll(List<Order> orders) {
        reactiveMongoTemplate.insertAll(orders).blockLast();
    }

    public Mono<Order> findById(@NonNull String orderId) {
        Query query = Query.query(Criteria.where("_id").is(orderId));

        return reactiveMongoTemplate.findOne(query, Order.class);
    }

    public Mono<Order> findOneByName(String name) {
        Query query = Query.query(Criteria.where("name").is("^" + name));

        return reactiveMongoTemplate.findOne(query, Order.class);
    }

    public Flux<Order> findByName(String name) {
        Query query = Query.query(Criteria.where("name").is("^" + name));

        return reactiveMongoTemplate.find(query, Order.class);
    }
}
