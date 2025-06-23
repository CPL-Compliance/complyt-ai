package com.complyt.business.transaction;

import com.complyt.business.transaction.data_fetcher.MatchedAddressFetcher;
import com.complyt.business.transaction.data_injector.TransactionMatchedAddressInjector;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class MatchedAddressProvider implements SalesTaxDataProvider<Transaction> {

    @NonNull
    private MatchedAddressFetcher addressFetcher;

    @NonNull
    private TransactionMatchedAddressInjector transactionMatchedAddressInjector;

    @Override
    public Mono<Transaction> provide(Transaction transaction) {
                return addressFetcher.fetch(transaction.getShippingAddress())
                        .flatMap(cityCountyWrapper -> transactionMatchedAddressInjector.inject(cityCountyWrapper, transaction));
    }
}