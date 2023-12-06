package com.complyt.repositories;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.security.TenantResolver;
import com.complyt.utils.observability.ContextLogger;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalesTaxTrackingRepository {

    @NonNull
    ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    TenantResolver tenantResolver;

    public Mono<SalesTaxTracking> findByState(@NonNull String state) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Criteria stateSearchCriteria = new Criteria()
                            .orOperator(Criteria.where("state.abbreviation").is(state),
                                    Criteria.where("state.name").is(state));

                    Query query = Query.query(stateSearchCriteria.and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for sales tax tracking with state "
                                    + state + " and tenant ID " + tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, SalesTaxTracking.class));
                });
    }

    public Mono<SalesTaxTracking> findByComplytId(@NonNull UUID complytId) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("complytId").is(complytId).and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for sales tax tracking by complyt ID " + complytId
                                    + " and tenant ID " + tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, SalesTaxTracking.class));
                });
    }

    public Mono<SalesTaxTracking> save(@NonNull SalesTaxTracking salesTaxTracking) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> ContextLogger.observeCtx("Saving sales tax tracking: " + salesTaxTracking, log::info)
                        .then(reactiveMongoTemplate.save(salesTaxTracking.withTenantId(tenantId))));
    }

    public Mono<SalesTaxTracking> findById(String id) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                     Query query = Query.query(Criteria.where("_id").is(id).and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for a sales tax tracking with ID "
                                    + id + " and tenant ID " + tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, SalesTaxTracking.class));
                });
    }

    public Flux<SalesTaxTracking> findAll(int offset, int limit) {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId))
                            .with(Sort.by(Sort.Direction.ASC, "_id")).skip(offset).limit(limit);

                    return ContextLogger.observeCtx("Searching for all sales tax tracking with tenant ID " + tenantId, log::info)
                            .thenMany(reactiveMongoTemplate.find(query, SalesTaxTracking.class));
                });
    }
}