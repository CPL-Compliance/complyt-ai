package com.complyt.repository;

import com.complyt.entity.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientRepository  extends MongoRepository<Client, String> {
    Client findByName(String name);
}