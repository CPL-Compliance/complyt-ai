package com.complyt.repositories;

import com.complyt.domain.Address;
import com.complyt.domain.ComplytSalesTaxRates;
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
public class ComplytSalesTaxRatesRepository {

    @NonNull
    ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    QueryBuilder<Address> addressQueryBuilder;

    public Mono<ComplytSalesTaxRates> findByAddress(@NonNull Address address, @NonNull String collection) {
        Query query = addressQueryBuilder.build(address);

        return ContextLogger.observeCtx("Searching for rates in " + collection + ", by address: " + query, log::info)
                .then(reactiveMongoTemplate.findOne(query, ComplytSalesTaxRates.class, collection));
    }

    public Mono<ComplytSalesTaxRates> save(@NonNull ComplytSalesTaxRates complytSalesTaxRates, @NonNull String collection) {
        return ContextLogger.observeCtx("Saving ComplytSalesTaxRates: " + complytSalesTaxRates, log::info)
                .then(reactiveMongoTemplate.save(complytSalesTaxRates, collection));
    }

}