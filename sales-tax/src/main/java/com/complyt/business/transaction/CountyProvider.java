package com.complyt.business.transaction;

import com.complyt.business.transaction.data_fetcher.CountyFetcher;
import com.complyt.business.transaction.data_injector.TransactionCountyInjector;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class CountyProvider implements SalesTaxDataProvider<Transaction> {

    @NonNull
    private CountyFetcher addressFetcher;

    public Mono<Transaction> provide(Transaction transaction) {
        return addressFetcher.fetch(transaction.getShippingAddress())
                .flatMap(county -> new TransactionCountyInjector(transaction).inject(county));
    }
}