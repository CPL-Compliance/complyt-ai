package com.complyt.repositories;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.Customer;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Slf4j
@AllArgsConstructor
public class TransactionRepository {
    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    private TenantResolver tenantResolver;

    public Mono<Transaction> save(@NonNull Transaction transaction) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Transaction transactionWithTenantId = transaction.withTenantId(tenantId);
                    return ContextLogger.observeCtx("Saving transaction: " + transactionWithTenantId, log::info)
                            .then(reactiveMongoTemplate.save(transactionWithTenantId)
                            .flatMap(savedTransaction -> reactiveMongoTemplate.findOne(Query.query(Criteria
                                            .where("complytId").is(transaction.getCustomerId())
                                            .and("tenantId").is(tenantId)), Customer.class)
                                    .map(savedTransaction::withCustomer)));
                });
    }

    public Flux<Transaction> saveAll(List<Transaction> transactions) {
        return ContextLogger.observeCtx("Saving transactions", log::info)
                .thenMany(tenantResolver.resolve()
                .map(tenantId -> transactions.stream().map(transaction -> transaction.withTenantId(tenantId)).collect(Collectors.toList()))
                .flatMapMany(transactionsWithClientId -> reactiveMongoTemplate.insertAll(transactionsWithClientId)
                        .flatMap(transaction -> reactiveMongoTemplate.findOne(Query.query(Criteria
                                        .where("complytId").is(transaction.getCustomerId())), Customer.class)
                                .map(transaction::withCustomer).switchIfEmpty(Mono.just(transaction)))));
    }

    public Mono<Transaction> findById(@NonNull String transactionId) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("_id").is(transactionId)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for transaction with id of: " + transactionId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, Transaction.class)
                                    .flatMap(transaction -> reactiveMongoTemplate
                                            .findOne(Query.query(Criteria
                                                    .where("complytId").is(transaction.getCustomerId())
                                                    .and("tenantId").is(tenantId)), Customer.class)
                                            .map(transaction::withCustomer)));
                });
    }

    public Mono<Transaction> findByExternalIdAndSource(String externalId, String source) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("externalId").is(externalId)
                            .and("source").is(source)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for an transaction with external id of: " + externalId + ", in source: " + source, log::info)
                            .then(reactiveMongoTemplate
                                    .findOne(query, Transaction.class)
                                    .flatMap(transaction -> reactiveMongoTemplate
                                            .findOne(Query.query(Criteria
                                                    .where("complytId").is(transaction.getCustomerId())
                                                    .and("tenantId").is(tenantId)), Customer.class)
                                            .map(transaction::withCustomer).switchIfEmpty(Mono.just(transaction))));
                });
    }

    public Mono<Transaction> findByComplytId(UUID complytId) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("complytId").is(complytId)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for an transaction with ComplytId of: " + complytId, log::info)
                            .then(reactiveMongoTemplate
                                    .findOne(query, Transaction.class)
                                    .flatMap(transaction -> reactiveMongoTemplate
                                            .findOne(Query.query(Criteria
                                                    .where("complytId").is(transaction.getCustomerId())
                                                    .and("tenantId").is(tenantId)), Customer.class)
                                            .map(transaction::withCustomer).switchIfEmpty(Mono.just(transaction))));
                });
    }

    public Flux<Transaction> findAll() {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Executing find tenant's related transactions", log::info)
                            .thenMany(reactiveMongoTemplate.find(query, Transaction.class)
                                    .flatMap(transaction -> reactiveMongoTemplate
                                            .findOne(Query.query(Criteria
                                                    .where("complytId").is(transaction.getCustomerId())
                                                    .and("tenantId").is(tenantId)), Customer.class)
                                            .map(transaction::withCustomer).switchIfEmpty(Mono.just(transaction))));
                });
    }

    public Flux<Transaction> findAllBySource(String source) {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId).and("source").is(source));

                    return ContextLogger.observeCtx("Executing find tenant's related transactions in source: " + source, log::info)
                            .thenMany(reactiveMongoTemplate.find(query, Transaction.class)
                                    .flatMap(transaction -> reactiveMongoTemplate
                                            .findOne(Query.query(Criteria
                                                    .where("complytId").is(transaction.getCustomerId())
                                                    .and("tenantId").is(tenantId)), Customer.class)
                                            .map(transaction::withCustomer).switchIfEmpty(Mono.just(transaction))));
                });
    }

    public Flux<Transaction> findAllByQuery(Query query) {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query updatedQuery = query.addCriteria(Criteria.where("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Executing find tenant's related transactions by query: " + query, log::info)
                            .thenMany(reactiveMongoTemplate.find(updatedQuery, Transaction.class)
                                    .flatMap(transaction -> reactiveMongoTemplate
                                            .findOne(Query.query(Criteria
                                                    .where("complytId").is(transaction.getCustomerId())
                                                    .and("tenantId").is(tenantId)), Customer.class)
                                            .map(transaction::withCustomer)));
                });
    }
}