package com.complyt.repositories;

import com.complyt.domain.Client;
import com.complyt.domain.Order;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ClientRepository {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<Client> save(@NonNull Client client) {
        return reactiveMongoTemplate.save(client);
    }

    public Mono<Client> findOneByName(@NonNull String name) {
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));
        return reactiveMongoTemplate.findOne(query, Client.class);
    }

    public void addOrderToClient(@NonNull String name, @NonNull Order order) {
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));
        Update update = new Update().push("orders", order);
        reactiveMongoTemplate.updateFirst(query, update, Client.class);
    }

    public Mono<Client> findOneById(String id) {
        return reactiveMongoTemplate.findById(id, Client.class);
    }

    public Flux<Client> findByName(String name) {
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));
        return reactiveMongoTemplate.find(query, Client.class);
    }
}