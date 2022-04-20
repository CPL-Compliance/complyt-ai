package com.complyt.repositories;

import com.complyt.domain.Order;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.mongodb.client.result.UpdateResult;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@Slf4j
public class OrderRepository {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    public Order save(@NonNull Order order) {
        return reactiveMongoTemplate.save(order).block();
    }

    public Flux<Order> insertAll(List<Order> orders) {
        return reactiveMongoTemplate.insertAll(orders);
    }

    public Mono<Order> findById(@NonNull String orderId) {
        Query query = Query.query(Criteria.where("_id").is(orderId));

        return reactiveMongoTemplate.findOne(query, Order.class);
    }

    public Mono<Order> findOneByName(String name) {
        throw new UnsupportedOperationException("findOneByName isn't implemented");
    }

    public Flux<Order> findByName(String name) {
        throw new UnsupportedOperationException("findByName isn't implemented");
    }

    public Mono<Order> upsert(@NonNull Order order) {
        String externalId = order.getExternalId();
        Query query = Query.query(Criteria.where("externalId").is(externalId));

        Update update = new Update()
                .set("externalId", order.getExternalId())
                .set("billingAddress", order.getBillingAddress())
                .set("shippingAddress", order.getShippingAddress())
                .set("customerId", order.getCustomerId())
                .set("items", order.getItems());

        UpdateResult updateResult = reactiveMongoTemplate.upsert(query, update, Order.class).block();

        if(!updateResult.wasAcknowledged())
        {
            log.error(String.format("Failed to write order into the data base, %s",order));
            throw new OperationFailedException(String.format("Could not update order, %s",order));
        }

        return findByExternalId(externalId);
    }

    public Mono<Order> findByExternalId(String externalId){
        Query query = Query.query(Criteria.where("externalId").is(externalId));

        return reactiveMongoTemplate.findOne(query, Order.class);
    }

    public Flux<Order> findAll() {
        return reactiveMongoTemplate.findAll(Order.class);
    }
}
