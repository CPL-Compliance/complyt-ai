package com.complyt.repositories.gt_rates;

import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.gt.ComplytGtRates;
import com.complyt.domain.gt.GtAddress;
import com.complyt.repositories.QueryBuilder;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@AllArgsConstructor
public class ComplytGtRatesRepository {

    @NonNull
    ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    QueryBuilder<GtAddress> gtCountriesQueryBuilder;

    public Mono<ComplytGtRates> findByAddress(@NonNull GtAddress gtAddress) {
        Query query = gtCountriesQueryBuilder.build(gtAddress);


        return ContextLogger.observeCtx("Searching for rates in gt_rates by requestAddress: " + query, log::info)
                .then(reactiveMongoTemplate.findOne(query, ComplytGtRates.class));
    }
}