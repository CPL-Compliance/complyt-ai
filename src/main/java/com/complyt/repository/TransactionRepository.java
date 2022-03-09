package com.complyt.repository;

import com.complyt.entity.State;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<State, String> {

}
