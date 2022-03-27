package com.complyt.repositories;

import com.complyt.domain.Order;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public Order save(@NonNull Order order){
        return mongoTemplate.save(order);
    }

    public void insertAll(List<Order> orders) {
        mongoTemplate.insertAll(orders);
    }

    public Order findById(@NonNull String orderId) {
        Query query = Query.query(Criteria.where("_id").is(orderId));
        Order order = mongoTemplate.findOne(query, Order.class);

        return order;
    }
}
