package io.complyt.repositories;

import io.complyt.business.address.CollectionNameResolver;
import io.complyt.domain.Address;
import io.complyt.domain.AddressQueryBuilder;
import io.complyt.domain.ValidatedAddress;
import io.complyt.security.TenantResolver;
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
    private AddressQueryBuilder addressQueryBuilder;

    public Mono<ValidatedAddress> saveAddress(@NonNull ValidatedAddress address) {
        String collection = CollectionNameResolver.resolve(address.getRequestAddress());

        return TenantResolver.resolve()
                .flatMap(tenantId -> ContextLogger.observeCtx("--> saving validated address of tenantId " + tenantId + ": " + address, log::info)
                .then(reactiveMongoTemplate.save(address, collection)));
    }

    public Mono<ValidatedAddress> findAddress(@NonNull Address address) {
        Query query = addressQueryBuilder.build(address);
        String collection = CollectionNameResolver.resolve(address);

        return TenantResolver.resolve()
                .flatMap(tenantId -> ContextLogger.observeCtx("--> find validated address of tenantId " + tenantId + ": " + address + " with query: " + query, log::info)
                .then(reactiveMongoTemplate.findOne(query, ValidatedAddress.class, collection)));

    }
}
