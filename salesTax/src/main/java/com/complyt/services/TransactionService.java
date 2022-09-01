package com.complyt.services;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.Customer;
import com.complyt.services.crud.CrudService;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService extends CrudService<Transaction, String> {
    Mono<Transaction> findByExternalId(@NonNull final String externalId);

    Mono<Transaction> update(@NonNull final String externalId, @NonNull final Transaction transaction);

    Mono<Transaction> markAsCancelled(@NonNull final String transactionId);

    Flux<Transaction> getTransactionsByQuery(@NonNull Query query);

    Mono<Transaction> injectDataToModifiedTransaction(@NonNull Transaction newTransaction, @NonNull Transaction oldTransaction);

    Mono<Transaction> injectDataToNewTransaction(@NonNull Transaction transaction, @NonNull Customer customer);
}