package com.complyt.repository;

import com.complyt.model.Order;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public Order save(@NotNull Order order){
        return mongoTemplate.save(order);
    }

    public void insertAll(List<Order> orders) {
        mongoTemplate.insertAll(orders);
    }

    public Order findById(@NotNull String orderId) {
        Query query = Query.query(Criteria.where("_id").is(orderId));
        Order order = mongoTemplate.findOne(query, Order.class);

        return order;
    }
}
