package com.complyt.repositories;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.Customer;
import com.complyt.security.TenantResolver;
import com.complyt.v1.exceptions.ObjectNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
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
                .flatMap(tenantId -> getCustomerByTransaction(transaction, tenantId)
                        .flatMap(customer -> reactiveMongoTemplate.save(transaction.withTenantId(tenantId))
                                .map(savedTransaction -> savedTransaction.withCustomer(customer))));
    }

    public Flux<Transaction> saveAll(@NonNull List<Transaction> transactions) {
        return tenantResolver.resolve()
                .map(tenantId -> transactions.stream().map(transaction -> transaction.withTenantId(tenantId)).collect(Collectors.toList()))
                .flatMapMany(transactionsWithClientId -> reactiveMongoTemplate.insertAll(transactionsWithClientId)
                        .flatMap(transaction -> getCustomerByTransaction(transaction, transaction.getTenantId())
                                .map(transaction::withCustomer))).log();
    }

    public Mono<Transaction> findById(@NonNull String transactionId) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("_id").is(transactionId)
                            .and("tenantId").is(tenantId));

                    log.debug("Searching for transaction with id of : " + transactionId);

                    return reactiveMongoTemplate
                            .findOne(query, Transaction.class)
                            .flatMap(transaction -> getCustomerByTransaction(transaction, tenantId)
                                    .map(transaction::withCustomer)).log();
                });
    }

    public Mono<Transaction> findByExternalId(@NonNull String externalId) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("externalId").is(externalId)
                            .and("tenantId").is(tenantId));
                    log.debug("Searching for an transaction with external id of : " + externalId);

                    return reactiveMongoTemplate
                            .findOne(query, Transaction.class)
                            .flatMap(transaction -> getCustomerByTransaction(transaction, tenantId)
                                    .map(transaction::withCustomer)).log();
                });
    }

    public Flux<Transaction> findAll() {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId));
                    log.debug("Executing find tenant's related transactions");
                    return reactiveMongoTemplate.find(query, Transaction.class)
                            .flatMap(transaction -> getCustomerByTransaction(transaction, tenantId)
                                    .map(transaction::withCustomer).switchIfEmpty(Mono.just(transaction))).log();
                });
    }

    public Flux<Transaction> findAllByQuery(Query query) {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    log.debug("Executing find tenant's related transactions by query : " + query);
                    Query updatedQuery = query.addCriteria(Criteria.where("tenantId").is(tenantId));

                    return reactiveMongoTemplate.find(updatedQuery, Transaction.class)
                            .flatMap(transaction -> getCustomerByTransaction(transaction, tenantId)
                                    .map(transaction::withCustomer)).log();
                });
    }

    private Mono<Customer> getCustomerByTransaction(Transaction transaction, String tenantId) {
        Query query = Query.query(Criteria.where("_id").is(transaction.getCustomerId())
                .and("tenantId").is(tenantId));

        return reactiveMongoTemplate.findOne(query, Customer.class)
                .switchIfEmpty(Mono.error(new NotFoundException("Transaction's Customer has not been found")));
    }
}