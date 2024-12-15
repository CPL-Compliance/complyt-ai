package io.complyt.repositories;

import io.complyt.business.collection_fetcher.UsaStatesMap;
import io.complyt.domain.Address;
import io.complyt.domain.UnitedStatesAddressQueryBuilder;
import io.complyt.domain.ValidatedAddress;
import io.complyt.utils.observability.ContextLogger;
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
public class ValidationAddressRepositoryImpl {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    private UnitedStatesAddressQueryBuilder unitedStatesAddressQueryBuilder;

    public Mono<ValidatedAddress> saveAddress(@NonNull ValidatedAddress address) {
        String collection = UsaStatesMap.statesToCollections.get(address.getRequestAddress().state().toUpperCase());
        return ContextLogger.observeCtx("--> saving validated address: " + address, log::info)
                .then(reactiveMongoTemplate.save(address, collection));
    }

    public Mono<ValidatedAddress> findAddress(@NonNull Address address) {
        Query query = unitedStatesAddressQueryBuilder.build(address);
        String collection = UsaStatesMap.statesToCollections.get(address.state().toUpperCase());

        return ContextLogger.observeCtx("--> find validated address: " + address + " with query: " + query, log::info)
                .then(reactiveMongoTemplate.findOne(query, ValidatedAddress.class, collection));

    }
}
