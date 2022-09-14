package com.complyt.utils.data_injector;

import com.complyt.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import reactor.core.publisher.Mono;

@Getter
@AllArgsConstructor
public class TransactionCountyInjector implements TransactionDataInjector<String>{

    @NonNull
    private final Transaction transaction;

    @Override
    public Mono<Transaction> inject(String county) {
        return Mono.just(transaction.withShippingAddress(transaction.getShippingAddress().withCounty(county)));
    }
}
