package com.complyt.business.transaction;

import com.complyt.business.transaction.data_fetcher.CityCountyFetcher;
import com.complyt.business.transaction.data_injector.TransactionCityCountyInjector;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class CityCountyProvider implements SalesTaxDataProvider<Transaction> {

    @NonNull
    private CityCountyFetcher addressFetcher;

    public Mono<Transaction> provide(Transaction transaction) {
        return addressFetcher.fetch(transaction.getShippingAddress())
                .flatMap(cityCountyWrapper -> new TransactionCityCountyInjector(transaction).inject(cityCountyWrapper));
    }
}