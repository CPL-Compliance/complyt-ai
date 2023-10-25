package com.complyt.business.transaction;

import com.complyt.business.transaction.data_fetcher.CityCountyStateFetcher;
import com.complyt.business.transaction.data_injector.TransactionCityCountyStateInjector;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class CityCountyStateProvider implements SalesTaxDataProvider<Transaction> {

    @NonNull
    private CityCountyStateFetcher addressFetcher;

    public Mono<Transaction> provide(Transaction transaction) {
        return addressFetcher.fetch(transaction.getShippingAddress())
                .flatMap(cityCountyStateWrapper -> new TransactionCityCountyStateInjector(transaction).inject(cityCountyStateWrapper));
    }
}