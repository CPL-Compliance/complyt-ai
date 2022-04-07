package com.complyt.repositories;

import com.complyt.domain.Customer;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@Slf4j
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

    public Customer upsert(@NonNull Customer customer) {
        String externalId = customer.getExternalId();
        Query query = Query.query(Criteria.where("externalId").is(externalId));

        Update update = new Update()
                .set("externalId", customer.getExternalId())
                .set("address", customer.getAddress())
                .set("name", customer.getName());

        UpdateResult updateResult = mongoTemplate.upsert(query, update, Customer.class);

        if(!updateResult.wasAcknowledged())
        {
            log.error(String.format("Failed to write customer into the data base, %s",customer.toString()));
            throw new OperationFailedException(String.format("Could not update customer, %s",customer.toString()));
        }

        return findByExternalId(externalId);
    }

    public Customer findByExternalId(String externalId){
        Query query = Query.query(Criteria.where("externalId").is(externalId));

        return mongoTemplate.findOne(query, Customer.class);
    }

    public Customer findById(BsonValue upsertedId) {
        return mongoTemplate.findById(upsertedId, Customer.class);
    }
}

