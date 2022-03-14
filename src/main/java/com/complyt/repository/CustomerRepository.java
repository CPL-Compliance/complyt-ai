package com.complyt.repository;

import com.complyt.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerRepository {
    @Autowired
    MongoTemplate mongoTemplate;

    public List<Customer> findByName(String name){
        return mongoTemplate.find(Query.query(Criteria.where("name").is(name)), Customer.class);
    }

    public Customer save(Customer customer){
        return mongoTemplate.save(customer);
    }
}
