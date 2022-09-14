package com.complyt.utils.data_injector;

import com.complyt.domain.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionDataInjector<T> {
    Mono<Transaction> inject(T t);
}
