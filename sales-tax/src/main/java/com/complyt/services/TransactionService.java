package com.complyt.services;

import com.complyt.domain.transaction.Transaction;
import com.complyt.services.crud.CrudService;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransactionService extends CrudService<Transaction, String> {
    Mono<Transaction> findByExternalIdAndSource(@NonNull final String externalId, @NonNull final String source);

    Mono<Transaction> findByComplytId(@NonNull final UUID complytId);

    Flux<Transaction> findAllBySource(@NonNull final String source);

    Mono<Transaction> update(@NonNull final String externalId, @NonNull String source, @NonNull final Transaction transaction);

    Mono<Transaction> markAsCancelled(@NonNull final String externalId, @NonNull final String source);

    Flux<Transaction> getTransactionsByQuery(@NonNull Query query);

    Mono<Transaction> injectDataToModifiedTransaction(@NonNull Transaction newTransaction, @NonNull Transaction oldTransaction);

    Mono<Transaction> injectDataToNewTransaction(@NonNull Transaction transaction);

    Mono<Transaction> checkComplytIdOfModifiedEqualsToOriginal(@NonNull final Transaction modifiedTransaction, @NonNull final Transaction originalTransaction);

    Mono<Transaction> checkTransactionNotHavingComplytId(@NonNull final Transaction newTransaction);

    Mono<Transaction> checkAfterTaxDiscountAndHandle(@NonNull final Transaction transaction);
}