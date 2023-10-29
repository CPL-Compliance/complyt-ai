package com.complyt.business.transaction.data_injector;

import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.CityCountyWrapper;
import com.complyt.domain.transaction.Transaction;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public record TransactionCityCountyInjector(@NonNull Transaction transaction) implements TransactionDataInjector<CityCountyWrapper> {

    @Override
    public Mono<Transaction> inject(CityCountyWrapper cityCountyWrapper) {
        Address modifiedAddress = transaction.getShippingAddress()
                .withCity(cityCountyWrapper.city())
                .withCounty(cityCountyWrapper.county());

        return Mono.just(transaction.withShippingAddress(modifiedAddress));
    }
}