package com.complyt.repositories;

import com.complyt.domain.transaction.GeoRecord;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@AllArgsConstructor
public class GeoRecordRepository {
    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<GeoRecord> findStateByZip(@NonNull String zipCode) {
        Query query = Query.query(Criteria.where("zip").is(zipCode));

        return ContextLogger.observeCtx("Searching for State with Zip Code " + zipCode, log::info)
                .then(reactiveMongoTemplate.findOne(query, GeoRecord.class));
    }

}