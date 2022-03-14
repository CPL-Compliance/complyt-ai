package com.complyt.repository;

import com.complyt.model.State;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<State, String> {

}
