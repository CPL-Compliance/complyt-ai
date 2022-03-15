package com.complyt.repository;

import com.complyt.model.Order;
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

    public Order save(Order order){
        return mongoTemplate.save(order);
    }

    public void insertAll(List<Order> orders) {
        mongoTemplate.insertAll(orders);
    }

    public Order findById(String orderId) {
        Order order = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(orderId)), Order.class);

        return order;
    }
}
