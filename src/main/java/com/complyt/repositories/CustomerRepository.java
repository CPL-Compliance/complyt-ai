package com.complyt.repositories;

import com.complyt.domain.Customer;
import lombok.NonNull;
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

    public List<Customer> findByName(@NonNull String name) {
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        return mongoTemplate.find(query, Customer.class);
    }

    public Customer findOneByName(@NonNull String name) {
        Query query = Query.query(Criteria.where("name").is("^" + name));

        return mongoTemplate.findOne(query, Customer.class);
    }

    public List<Customer> getAllCustomers() {

        return mongoTemplate.findAll(Customer.class);
    }

    public Customer save(@NonNull Customer customer) {

        return mongoTemplate.save(customer);
    }
}
