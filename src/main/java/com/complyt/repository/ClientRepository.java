package com.complyt.repository;

import com.complyt.model.Client;
import com.complyt.model.Order;
import com.mongodb.client.result.UpdateResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Locale;

@Repository
public class ClientRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public Client save(@NotNull Client client){
        Client clientResult =  mongoTemplate.save(client);

        return clientResult;
    }

    public Client findOne(@NotNull String name){
        Query query = Query.query(Criteria.where("name").regex(name, "i"));
        Client client = mongoTemplate.findOne(query, Client.class);

        return client;
    }

    public void addOrderToClient(@NotNull String name, @NotNull Order order){
        Query query = Query.query(Criteria.where("name").regex(name, "i"));
        Update update = new Update().push("orders", order);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Client.class);
    }
}