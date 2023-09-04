package com.complyt.repositories;

import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.security.TenantResolver;
import com.complyt.utils.observability.ContextLogger;
import com.mongodb.client.result.DeleteResult;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@Slf4j
@AllArgsConstructor
public class ExemptionRepository {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    private TenantResolver tenantResolver;

    public Mono<Exemption> findByClientCustomerAndState(@NonNull final Transaction transaction) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId)
                            .and("customerId").is(transaction.getCustomerId())
                            .and("state.abbreviation").is(transaction.getShippingAddress().state()));

                    return ContextLogger.observeCtx("Searching for exemption by query: " + query, log::info)
                            .then(reactiveMongoTemplate.findOne(query, Exemption.class));
                });
    }

    public Mono<Exemption> save(@NonNull final Exemption exemption) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Exemption exemptionWithTenantId = exemption.withTenantId(tenantId);

                    return ContextLogger.observeCtx("Saving exemption: " + exemptionWithTenantId, log::info)
                            .then(reactiveMongoTemplate.save(exemptionWithTenantId));
                });
    }

    @Deprecated
    public Mono<Exemption> findById(@NonNull final String id) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("_id").is(id)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for exemption with ID " + id + " and tenant ID " + tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, Exemption.class));
                });
    }

    public Mono<Exemption> findByComplytId(@NonNull final UUID complytId) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("complytId").is(complytId)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for exemption with complyt ID " + complytId + " and tenant ID " + tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, Exemption.class));
                });
    }

    public Flux<Exemption> findAll() {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for exemptions with tenant ID " + tenantId, log::info)
                            .thenMany(reactiveMongoTemplate.find(query, Exemption.class));
                });
    }

    public Mono<DeleteResult> delete(@NonNull final UUID complytId) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("complytId").is(complytId).and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Deleting exemption with complyt ID " + complytId + " and tenant ID " + tenantId, log::info)
                            .then(reactiveMongoTemplate.remove(query, Exemption.class));
                });
    }
}