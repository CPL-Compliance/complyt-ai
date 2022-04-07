package com.complyt.repositories;

import com.complyt.domain.Customer;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public class CustomerRepository {
    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    public Flux<Customer> findByName(@NonNull String name) {
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        return reactiveMongoTemplate.find(query, Customer.class);
    }

    public Mono<Customer> findOneByName(@NonNull String name) {
        Query query = Query.query(Criteria.where("name").is("^" + name));

        return reactiveMongoTemplate.findOne(query, Customer.class);
    }

    public Flux<Customer> getAllCustomers() {
        return reactiveMongoTemplate.findAll(Customer.class);
    }

    public Mono<Customer> save(@NonNull Customer customer) {
        return reactiveMongoTemplate.save(customer);
    }

    public Mono<Customer> findById(String id) {
        return reactiveMongoTemplate.findById(id, Customer.class);
    }
}
