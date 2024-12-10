package com.complyt.business.transaction.data_injector;

import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.CityCountyWrapper;
import com.complyt.domain.transaction.Transaction;
import com.complyt.utils.observability.ContextLogger;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public record TransactionCityCountyInjector() implements TransactionDataInjector<CityCountyWrapper> {

    @Override
    public Mono<Transaction> inject(CityCountyWrapper cityCountyWrapper, @NonNull Transaction transaction) {
        Address modifiedAddress = transaction.getShippingAddress()
                .withCity(cityCountyWrapper.city())
                .withCounty(cityCountyWrapper.county());

        return ContextLogger.observeCtx("Inject City County to shipping Address " + modifiedAddress, log::info)
                .then(Mono.just(transaction.withShippingAddress(modifiedAddress)));
    }
}