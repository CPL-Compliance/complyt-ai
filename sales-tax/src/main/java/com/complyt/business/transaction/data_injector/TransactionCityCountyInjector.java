package com.complyt.business.transaction.data_injector;

import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.CityCountyWrapper;
import com.complyt.domain.transaction.Transaction;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public record TransactionCityCountyInjector() implements TransactionDataInjector<CityCountyWrapper> {

    @Override
    public Mono<Transaction> inject(CityCountyWrapper cityCountyWrapper, @NonNull Transaction transaction) {
        Address modifiedAddress = transaction.getShippingAddress()
                .withCity(cityCountyWrapper.city())
                .withCounty(cityCountyWrapper.county());

        return Mono.just(transaction.withShippingAddress(modifiedAddress));
    }
}