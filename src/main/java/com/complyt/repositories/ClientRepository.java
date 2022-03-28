package com.complyt.repositories;

import com.complyt.domain.Client;
import com.complyt.domain.Order;
import com.mongodb.client.result.UpdateResult;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClientRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public Client save(@NonNull Client client) {
        Client clientResult = mongoTemplate.save(client);

        return clientResult;
    }

    public Client findOneByName(@NonNull String name) {
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));
        Client client = mongoTemplate.findOne(query, Client.class);

        return client;
    }

    public void addOrderToClient(@NonNull String name, @NonNull Order order) {
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));
        Update update = new Update().push("orders", order);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Client.class);
    }

    public Client findOneById(String id) {
        return mongoTemplate.findById(id, Client.class);
    }

    public List<Client> findByName(String name) {
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));
        return mongoTemplate.find(query, Client.class);
    }
}