package com.complyt.business.transaction.data_injector;

import com.complyt.domain.transaction.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionDataInjector<T> {
    Mono<Transaction> inject(T t, Transaction transaction);

    default boolean shouldInject(T t, Transaction transaction) {
        return true;
    }
}