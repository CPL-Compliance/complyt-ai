package com.complyt.business.utils.data_injector;

import com.complyt.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Getter
@AllArgsConstructor
@Slf4j
public class TransactionCountyInjector implements TransactionDataInjector<String>{

    @NonNull
    private final Transaction transaction;

    @Override
    public Mono<Transaction> inject(String county) {
        return Mono.just(transaction.withShippingAddress(transaction.getShippingAddress().withCounty(county)));
    }
}
