package com.complyt.business.utils.transaction_data_injector;

import com.complyt.domain.Transaction;
import reactor.core.publisher.Mono;

public interface CountyInjector {
    Mono<Transaction> inject(Transaction transaction);
}
