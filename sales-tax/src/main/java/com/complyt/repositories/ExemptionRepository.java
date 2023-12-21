package com.complyt.repositories;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.security.TenantResolver;
import com.complyt.utils.observability.ContextLogger;
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

    public Mono<Exemption> findByCustomerAndState(@NonNull UUID customerId, @NonNull String state) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId)
                                    .and("customerId").is(customerId))
                            .addCriteria(new Criteria().orOperator(
                                    Criteria.where("state.abbreviation").is(state),
                                    Criteria.where("state.name").is(state)));

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

    public Flux<Exemption> findAll(int page, int size) {
        int calculatedOffset = (page - 1) * size;
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId))
                            .skip(calculatedOffset).limit(size);
                    return ContextLogger.observeCtx("Searching for exemptions with tenant ID " + tenantId + " with page " + page + " and size " + size, log::info)
                            .thenMany(reactiveMongoTemplate.find(query, Exemption.class));
                });
    }

}