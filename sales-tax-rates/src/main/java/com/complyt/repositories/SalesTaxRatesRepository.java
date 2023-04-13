package com.complyt.repositories;

import com.complyt.domain.Address;
import com.complyt.domain.AddressWithSalesTaxRates;
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
public class SalesTaxRatesRepository {

    @NonNull
    ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<AddressWithSalesTaxRates> findByAddress(@NonNull Address address, @NonNull String collection) {
        Query query = Query.query(Criteria
                .where("address.city").is(address.getCity())
                .and("address.street").is(address.getStreet())
                .and("address.zip").is(address.getZip())
        );

        return ContextLogger.observeCtx("Searching for rates in " + collection + ", by address: " + query, log::debug)
                .then(reactiveMongoTemplate.findOne(query, AddressWithSalesTaxRates.class, collection));
    }

    public Mono<AddressWithSalesTaxRates> save(@NonNull AddressWithSalesTaxRates addressWithSalesTaxRates, @NonNull String collection) {
        return ContextLogger.observeCtx("Saving address with rates: " + addressWithSalesTaxRates, log::debug)
                .then(reactiveMongoTemplate.save(addressWithSalesTaxRates, collection));
    }

}