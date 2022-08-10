package com.complyt.business.utils.transaction_data_injector;

import com.complyt.domain.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionDataInjector<T> {
    Mono<Transaction> inject(T t);
}
