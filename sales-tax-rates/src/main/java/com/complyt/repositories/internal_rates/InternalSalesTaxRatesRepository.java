package com.complyt.repositories.internal_rates;

import com.complyt.business.internal_sales_tax_rates.InternalRatesCollectionNames;
import com.complyt.domain.AddressWithDate;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.security.TenantResolver;
import com.complyt.utils.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@Repository
@AllArgsConstructor
@Slf4j
public class InternalSalesTaxRatesRepository {

    @NonNull
    TenantResolver tenantResolver;

    @NonNull
    ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    InternalRatesAddressQueryBuilder internalRatesAddressQueryBuilder;

    public Mono<InternalSalesTaxRates> find(@NonNull AddressWithDate addressWithDate) {
        Query query = internalRatesAddressQueryBuilder.build(addressWithDate.getAddress());
        String state = addressWithDate.getAddress().state();

        return tenantResolver.resolve()
                .flatMap(tenantId -> ContextLogger.observeCtx("finding rate of tenantId " + tenantId + " with address" + addressWithDate + " using query:" + query, log::info)
                .then(reactiveMongoTemplate.findOne(query, InternalSalesTaxRates.class, getCollectionName(state))));
    }

    public Mono<InternalSalesTaxRates> save(@NonNull InternalSalesTaxRates internalSalesTaxRates) {
        String state = internalSalesTaxRates.getAddress().state();

        return reactiveMongoTemplate.save(internalSalesTaxRates, getCollectionName(state))
                .doOnSuccess(cachedInternalRate -> log.info("Saving internal rate: {}", cachedInternalRate));
    }

    private String getCollectionName(String state) {
        return InternalRatesCollectionNames.stateInternalCollectionName(state);
    }

    public Mono<InternalSalesTaxRates> archive(@NonNull InternalSalesTaxRates internalSalesTaxRates) {
        String state = internalSalesTaxRates.getAddress().state();
        Query query = internalRatesAddressQueryBuilder.build(internalSalesTaxRates);

        return reactiveMongoTemplate.findAndRemove(query, InternalSalesTaxRates.class, getCollectionName(state))
                .doOnNext(cachedInternalRate -> log.info("Archiving cached rate with complytId: {}", cachedInternalRate.getComplytId()))
                .flatMap(cachedInternalRate -> reactiveMongoTemplate.save(cachedInternalRate.setExpiredDate(LocalDateTime.now()).setId(null), InternalRatesCollectionNames.ARCHIVED_COLLECTION_NAME));
    }

    public Mono<InternalSalesTaxRates> updateRate(@NonNull InternalSalesTaxRates updatedRate) {
        String state = updatedRate.getAddress().state();
        Query query = internalRatesAddressQueryBuilder.build(updatedRate);

        return reactiveMongoTemplate.findOne(query, InternalSalesTaxRates.class, getCollectionName(state))
                .flatMap(foundInternalRate -> reactiveMongoTemplate.findAndReplace(query, updatedRate.setUpdatedFrom(foundInternalRate.getComplytId()), FindAndReplaceOptions.options(), getCollectionName(state))
                .doOnNext(cachedInternalRate ->   log.info("Archiving old rate with complytId: {} to new rate: {}", cachedInternalRate.getComplytId(), updatedRate))
                .flatMap(oldInternalRate -> reactiveMongoTemplate.save(oldInternalRate.setExpiredDate(LocalDateTime.now()).setUpdatedTo(updatedRate.getComplytId()).setId(null), InternalRatesCollectionNames.ARCHIVED_COLLECTION_NAME)));
    }
}

