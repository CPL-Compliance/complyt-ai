package com.complyt.repositories;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.security.TenantResolver;
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
                            .and("state.abbreviation").is(transaction.getShippingAddress().getState()));

                    log.debug("Searching for an exemption by query : " + query);

                    return reactiveMongoTemplate.findOne(query, Exemption.class).log();
                });
    }

    public Mono<Exemption> save(@NonNull final Exemption exemption) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> reactiveMongoTemplate.save(exemption.withTenantId(tenantId))).log();
    }

    public Mono<Exemption> findById(@NonNull final String id) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("_id").is(id)
                            .and("tenantId").is(tenantId));
                    log.debug("Searching for an exemption with id of : " + id);

                    return reactiveMongoTemplate.findOne(query, Exemption.class).log();
                });
    }

    public Flux<Exemption> findAll() {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId));
                    log.debug("Executing findAll exemptions");

                    return reactiveMongoTemplate.find(query, Exemption.class).log();
                });
    }

    public Mono<DeleteResult> delete(@NonNull final String id) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("_id").is(id).and("tenantId").is(tenantId));
                    log.debug("Deleting exemption with id : " + id);

                    return reactiveMongoTemplate.remove(query, Exemption.class);
                });
    }
}
