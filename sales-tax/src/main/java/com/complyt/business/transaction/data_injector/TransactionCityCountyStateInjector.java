package com.complyt.business.transaction.data_injector;

import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.CityCountyStateWrapper;
import com.complyt.domain.transaction.Transaction;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public record TransactionCityCountyStateInjector(@NonNull Transaction transaction) implements TransactionDataInjector<CityCountyStateWrapper> {

    @Override
    public Mono<Transaction> inject(CityCountyStateWrapper cityCountyStateWrapper) {
        Address modifiedAddress = transaction.getShippingAddress()
                .withCity(cityCountyStateWrapper.city())
                .withCounty(cityCountyStateWrapper.county())
                .withState(cityCountyStateWrapper.state());

        return Mono.just(transaction.withShippingAddress(modifiedAddress));
    }
}