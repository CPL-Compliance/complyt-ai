package com.complyt.repository;

import com.complyt.domain.State;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class StateRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public State findByName(@NotNull String name){
        Query query = Query.query(Criteria.where("name").regex(name, "i"));
        State state = mongoTemplate.findOne(query, State.class);

        return state;
    }
}