package com.complyt.repository;

import com.complyt.entity.State;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends MongoRepository<State, String> {
    List<State> findByName(String name);
}