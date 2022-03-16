package com.complyt.repository;

import com.complyt.model.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StateRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public State findByName(String name){
        return mongoTemplate.findOne(Query.query(Criteria.where("name").is(name)), State.class);
    }
}