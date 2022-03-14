package com.complyt.repository;

import com.complyt.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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
}
