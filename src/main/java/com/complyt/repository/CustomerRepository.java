package com.complyt.repository;

import com.complyt.domain.Customer;
import org.jetbrains.annotations.NotNull;
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

    public List<Customer> findByName(@NotNull String name){
        return mongoTemplate.find(Query.query(Criteria.where("name").is(name)), Customer.class);
    }

    public Customer findOneByName(@NotNull String name){
        return mongoTemplate.findOne(Query.query(Criteria.where("name").is(name)), Customer.class);
    }

    public List<Customer> getAllCustomers(){
        return mongoTemplate.findAll(Customer.class);
    }

    public Customer save(@NotNull Customer customer){
        return mongoTemplate.save(customer);
    }
}
