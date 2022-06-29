package com.complyt.services;

import com.complyt.domain.Transaction;
import lombok.NonNull;
import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TransactionService extends CrudService<Transaction, String> {
    void save(List<ObjectId> transactions);
    Mono<Transaction> findByExternalId(@NonNull final String externalId);
    Mono<Transaction> upsert(@NonNull final String externalId, @NonNull final Transaction transaction);
    Mono<Transaction> update(@NonNull final String externalId, @NonNull final Transaction transaction);
    Mono<Transaction> markAsCancelled(@NonNull final  String transactionId);
}