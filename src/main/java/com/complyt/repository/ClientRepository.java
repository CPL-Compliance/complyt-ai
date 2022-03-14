package com.complyt.repository;

import com.complyt.model.Client;
import com.complyt.model.Order;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class ClientRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public Client save(Client client){
        return mongoTemplate.save(client);
    }

    public void addOrderToClient(String name, Order order){
        UpdateResult updateResult = mongoTemplate.updateFirst(Query.query(Criteria.where("name").is(name)), new Update().push("orders", order), Client.class);
    }
}