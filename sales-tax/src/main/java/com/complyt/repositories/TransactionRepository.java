package com.complyt.repositories;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.Transaction;
import com.complyt.domain.security.User;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Repository;
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


    public Mono<Transaction> save(@NonNull Transaction transaction) {

        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> reactiveMongoTemplate.save(transaction.withClientId(user.getClientId()))
                        .flatMap(savedTransaction -> reactiveMongoTemplate.findById(savedTransaction.getCustomerId(), Customer.class)
                                .map(savedTransaction::withCustomer)));
    }

    public Flux<Transaction> saveAll(List<Transaction> transactions) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .map(user -> transactions.stream().map(transaction -> transaction.withClientId(user.getClientId())).collect(Collectors.toList()))
                .flatMapMany(transactionsWithClientId -> reactiveMongoTemplate.insertAll(transactionsWithClientId)
                        .flatMap(transaction -> reactiveMongoTemplate.findById(transaction.getCustomerId(), Customer.class)
                                .map(transaction::withCustomer))).log();
    }

    public Mono<Transaction> findById(@NonNull String transactionId) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    Query query = Query.query(Criteria.where("_id").is(transactionId)
                            .and("clientId").is(user.getClientId()));
                    log.debug("Searching for an transaction with id of : " + transactionId);

                    return reactiveMongoTemplate
                            .findOne(query, Transaction.class)
                            .flatMap(transaction -> reactiveMongoTemplate
                                    .findById(transaction.getCustomerId(), Customer.class)
                                    .map(transaction::withCustomer));
                });
    }

    public Mono<Transaction> findByExternalId(String externalId) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    Query query = Query.query(Criteria.where("externalId").is(externalId)
                            .and("clientId").is(user.getClientId()));
                    log.debug("Searching for an transaction with external id of : " + externalId);

                    return reactiveMongoTemplate
                            .findOne(query, Transaction.class)
                            .flatMap(transaction -> reactiveMongoTemplate
                                    .findById(transaction.getCustomerId(), Customer.class)
                                    .map(transaction::withCustomer));
                });
    }

    public Flux<Transaction> findAll() {
        return ReactiveSecurityContextHolder.getContext().log()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal()).log()
                .flatMapMany(user -> {
                    Query query = Query.query(Criteria.where("clientId").is(user.getClientId()));
                    log.debug("Executing find client's related transactions");
                    return reactiveMongoTemplate.find(query, Transaction.class)
                            .flatMap(transaction -> reactiveMongoTemplate.findById(transaction.getCustomerId(), Customer.class)
                                    .map(transaction::withCustomer));
                });
    }

    public Flux<Transaction> findAllByQuery(Query query) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (User) securityContext.getAuthentication().getPrincipal())
                .flatMapMany(user -> {
                    log.debug("Executing find client's related transactions by query : " + query);
                    Query updatedQuery = query.addCriteria(Criteria.where("clientId").is(user.getClientId()));

                    return reactiveMongoTemplate.find(updatedQuery, Transaction.class).log()
                            .flatMap(transaction -> reactiveMongoTemplate.findById(transaction.getCustomerId(), Customer.class).log()
                                    .map(transaction::withCustomer));
                });
    }
}