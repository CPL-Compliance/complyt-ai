package com.complyt.repositories;

import com.complyt.domain.State;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class StateRepository {

    @NonNull
    MongoTemplate mongoTemplate;

    public State findOneByName(@NonNull String name) {
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        return mongoTemplate.findOne(query, State.class);
    }

    public State save(@NonNull State state) {
        return mongoTemplate.save(state);
    }

    public State findById(String id) {
        return mongoTemplate.findById(id, State.class);
    }

    public List<State> findByName(String name) {
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        return mongoTemplate.find(query, State.class);
    }
}