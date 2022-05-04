package com.complyt.repositories;

import com.complyt.domain.Customer;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.mongodb.client.result.UpdateResult;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
@Slf4j
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

    public Flux<Customer> findAll() {
        return reactiveMongoTemplate.findAll(Customer.class);
    }

    public Customer save(@NonNull Customer customer) {
        return reactiveMongoTemplate.save(customer).block();
    }

    public Mono<Customer> findById(String id) {
        return reactiveMongoTemplate.findById(id, Customer.class);
    }

    public Mono<Customer> upsert(@NonNull Customer customer) {
        String externalId = customer.getExternalId();
        Query query = Query.query(Criteria.where("externalId").is(externalId));

        Update update = new Update()
                .set("externalId", customer.getExternalId())
                .set("address", customer.getAddress())
                .set("name", customer.getName());

        UpdateResult updateResult = reactiveMongoTemplate.upsert(query, update, Customer.class).block();
        if(!updateResult.wasAcknowledged())
        {
            log.error(String.format("Failed to write customer into the data base, %s",customer));
            throw new OperationFailedException(String.format("Could not update customer, %s",customer));
        }

        return findByExternalId(externalId);
    }

    public Mono<Customer> findByExternalId(String externalId){
        Query query = Query.query(Criteria.where("externalId").is(externalId));

        return reactiveMongoTemplate.findOne(query, Customer.class);
    }

    public Mono<Customer> findById(ObjectId upsertedId) {
        return reactiveMongoTemplate.findById(upsertedId, Customer.class);
    }
}