package com.complyt.repositories;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.security.TenantResolver;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Repository
public class SalesTaxTrackingRepository {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    private TenantResolver tenantResolver;

    public Mono<SalesTaxTracking> findByState(@NonNull String state) {


        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Criteria stateSearchCriteria = new Criteria()
                            .orOperator(Criteria.where("state.abbreviation").is(state),
                                    Criteria.where("state.name").is(state));

                    Query query = Query.query(stateSearchCriteria.and("tenantId").is(tenantId));

                    return reactiveMongoTemplate.findOne(query, SalesTaxTracking.class).log();
                });
    }

    public Mono<SalesTaxTracking> save(@NonNull SalesTaxTracking salesTaxTracking) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    log.debug("Saving modified sales tax tracking : " + salesTaxTracking);

                    return reactiveMongoTemplate.save(salesTaxTracking.withTenantId(tenantId)).log();
                });
    }

    public Mono<SalesTaxTracking> findById(String id) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("_id").is(id)
                            .and("tenantId").is(tenantId));

                    log.debug("Searching for a sales tax tracking with id of : " + id);

                    return reactiveMongoTemplate.findOne(query, SalesTaxTracking.class).log();
                });
    }

    public Flux<SalesTaxTracking> findAll() {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId));
                    log.debug("Executing findAll sales tax tracking");

                    return reactiveMongoTemplate.find(query, SalesTaxTracking.class).log();
                });
    }
}
