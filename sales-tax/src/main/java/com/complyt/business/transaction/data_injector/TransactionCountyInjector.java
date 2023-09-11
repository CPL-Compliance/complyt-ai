package com.complyt.business.transaction.data_injector;

import com.complyt.domain.transaction.Transaction;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public record TransactionCountyInjector(@NonNull Transaction transaction) implements TransactionDataInjector<String> {

    @Override
    public Mono<Transaction> inject(String county) {
        return Mono.just(transaction.withShippingAddress(transaction.getShippingAddress().withCounty(county)));
    }
}