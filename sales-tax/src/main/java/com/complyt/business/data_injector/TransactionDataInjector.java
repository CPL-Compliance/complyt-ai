package com.complyt.business.data_injector;

import com.complyt.domain.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionDataInjector<T> {
    Mono<Transaction> inject(T t);

    default boolean shouldExecute(T t){
        return true;
    }
}
