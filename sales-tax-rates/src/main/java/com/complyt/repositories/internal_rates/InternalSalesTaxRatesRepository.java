package com.complyt.repositories.internal_rates;

import com.complyt.business.internal_sales_tax_rates.InternalRatesCollectionNames;
import com.complyt.domain.Address;
import com.complyt.domain.AddressWithDate;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.repositories.QueryBuilder;
import com.complyt.utils.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
@AllArgsConstructor
@Slf4j
public class InternalSalesTaxRatesRepository {

    @NonNull
    ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    QueryBuilder<Address> internalRatesAddressQueryBuilder;

    public Mono<InternalSalesTaxRates> find(@NonNull AddressWithDate addressWithDate) {
        Query query = internalRatesAddressQueryBuilder.build(addressWithDate.getAddress());
        String state = addressWithDate.getAddress().state();

        return ContextLogger.observeCtx("finding rate with address" + addressWithDate + " using query:" + query, log::info)
                .then(reactiveMongoTemplate.findOne(query, InternalSalesTaxRates.class, getCollectionName(state)));
    }

    public Mono<InternalSalesTaxRates> save(@NonNull InternalSalesTaxRates internalSalesTaxRates) {
        String state = internalSalesTaxRates.getAddress().state();
        return reactiveMongoTemplate.save(internalSalesTaxRates, getCollectionName(state));
    }

    private String getCollectionName(String state) {
        return InternalRatesCollectionNames.stateInternalCollectionName(state);
    }
}

