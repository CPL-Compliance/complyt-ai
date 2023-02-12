package com.complyt.repositories;

import com.complyt.domain.customer.Customer;
import com.complyt.security.TenantResolver;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.logging.Level;

@Repository
@Slf4j
@AllArgsConstructor
public class CustomerRepository {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    private TenantResolver tenantResolver;

    public Flux<Customer> findByName(@NonNull String name) {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("name").regex("^" + name, "i")
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for customers with name : " + name, log::debug)
                            .thenMany(reactiveMongoTemplate.find(query, Customer.class));
                });
    }

    public Mono<Customer> findOneByName(@NonNull String name) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("name").is("^" + name)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for a customer with name : " + name, log::debug)
                            .then(reactiveMongoTemplate.findOne(query, Customer.class));
                });
    }

    public Flux<Customer> findAll() {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Executing findAll customers", log::info)
                            .thenMany(reactiveMongoTemplate.find(query, Customer.class));
                });
    }

    public Flux<Customer> findAllBySource(String source) {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId)
                            .and("source").is(source));

                    return ContextLogger.observeCtx("Executing findAll customers in source : " + source, log::debug)
                            .thenMany(reactiveMongoTemplate.find(query, Customer.class));
                });
    }

    public Mono<Customer> save(@NonNull Customer customer) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> reactiveMongoTemplate.save(customer.withTenantId(tenantId)));
    }

    public Mono<Customer> findById(String id) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("_id").is(id).and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for a customer with id of : " + id, log::debug)
                            .then(reactiveMongoTemplate.findOne(query, Customer.class));
                });
    }

    public Mono<Customer> findByExternalIdAndSource(String externalId, String source) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("externalId").is(externalId)
                            .and("source").is(source)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for a customer with externalId of : "
                                    + externalId + " in source : " + source, log::debug)
                            .then(reactiveMongoTemplate.findOne(query, Customer.class));
                });
    }

    public Mono<Customer> findByComplytId(UUID complytId) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("complytId").is(complytId)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for a customer with complytId of : " + complytId, log::debug)
                            .then(reactiveMongoTemplate.findOne(query, Customer.class));
                });
    }

    public Mono<Customer> findById(ObjectId id) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("_id").is(id)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Executing findById with search criteria of customer id : " + id.toString(), log::debug)
                            .then(reactiveMongoTemplate.findOne(query, Customer.class));
                });
    }
}