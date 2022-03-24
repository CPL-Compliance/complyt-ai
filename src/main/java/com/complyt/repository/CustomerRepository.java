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

    public List<Customer> findByName(@NotNull String name) {
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));
        List<Customer> customers = mongoTemplate.find(query, Customer.class);

        return customers;
    }

    public Customer findOneByName(@NotNull String name) {
        Query query = Query.query(Criteria.where("name").is("^" + name));
        Customer customer = mongoTemplate.findOne(query, Customer.class);

        return customer;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers =  mongoTemplate.findAll(Customer.class);

        return customers;
    }

    public Customer save(@NotNull Customer customer) {
        Customer returnedCustomer = mongoTemplate.save(customer);

        return returnedCustomer;
    }
}
