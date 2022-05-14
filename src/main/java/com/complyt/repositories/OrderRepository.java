package com.complyt.repositories;

import com.complyt.domain.Order;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.mongodb.client.result.UpdateResult;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
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
@AllArgsConstructor
public class OrderRepository {
    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    private MongoTemplate mongoTemplate;

    public Mono<Order> save(@NonNull Order order) {
        return reactiveMongoTemplate.save(order);
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

    public Mono<Order> upsertSync(@NonNull Order order) {
        String externalId = order.getExternalId();
        Query query = Query.query(Criteria.where("externalId").is(externalId));

        Update update = buildUpdateCommand(order);
        UpdateResult updateResult = mongoTemplate.upsert(query, update, Order.class);
        if (!updateResult.wasAcknowledged()) {
            log.error(String.format("Failed to write order into the data base, %s", order));
            throw new OperationFailedException(String.format("Could not update order, %s", order));
        }

        return findByExternalId(externalId);
    }

    public Order updateSync(@NonNull Order order) {
        Query query = Query.query(Criteria.where("externalId").is(order.getExternalId()));
        Update update = buildUpdateCommand(order);

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Order.class);
        if (!updateResult.wasAcknowledged()) {
            log.error(String.format("Failed to write order into the data base, %s", order));
            throw new OperationFailedException(String.format("Could not update order, %s", order));
        }

        return findByExternalIdSync(order.getExternalId());
    }

    public Mono<Order> update(Order order) {
        return Mono.just(order).flatMap(item -> {
            String externalId = item.getExternalId();
            Query query = Query.query(Criteria.where("externalId").is(externalId));
            Update update = buildUpdateCommand(item);

            return reactiveMongoTemplate.findAndModify(query, update, Order.class);
        });
    }

    public Mono<Order> findByExternalId(String externalId) {
        Query query = Query.query(Criteria.where("externalId").is(externalId));

        return reactiveMongoTemplate.findOne(query, Order.class);
    }

    public Flux<Order> findAll() {
        return reactiveMongoTemplate.findAll(Order.class);
    }

    public Order findByExternalIdSync(String externalId) {
        Query query = Query.query(Criteria.where("externalId").is(externalId));

        return mongoTemplate.findOne(query, Order.class);
    }

    public Update buildUpdateCommand(Order order) {
        return new Update()
                .set("externalId", order.getExternalId())
                .set("billingAddress", order.getBillingAddress())
                .set("shippingAddress", order.getShippingAddress())
                .set("customerId", order.getCustomerId())
                .set("items", order.getItems())
                .set("orderStatus", order.getOrderStatus())
                .set("salesTax", order.getSalesTax());
    }
}
