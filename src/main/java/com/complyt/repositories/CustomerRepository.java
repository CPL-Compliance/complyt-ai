package com.complyt.repositories;

import com.complyt.domain.Customer;
import com.mongodb.client.result.UpdateResult;
import lombok.NonNull;
import org.bson.BsonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    public Customer save(@NonNull Customer customer){
        return mongoTemplate.save(customer);
    }

    public UpdateResult update(@NonNull Customer customer){
        Query query = Query.query(Criteria.where("externalId").is(customer.getExternalId()));
        Update update = new Update()
                .set("address", customer.getAddress())
                .set("name", customer.getName());

        return mongoTemplate.updateFirst(query,update,Customer.class);
    }

    public UpdateResult upsert(@NonNull Customer customer) {
        Query query = Query.query(Criteria.where("externalId").is(customer.getExternalId()));

        Update update = new Update()
                .set("externalId", customer.getExternalId())
                .set("address", customer.getAddress())
                .set("name", customer.getName());

        return mongoTemplate.upsert(query, update, Customer.class);
    }

    public Customer findById(BsonValue upsertedId) {
        return mongoTemplate.findById(upsertedId, Customer.class);
    }
}

