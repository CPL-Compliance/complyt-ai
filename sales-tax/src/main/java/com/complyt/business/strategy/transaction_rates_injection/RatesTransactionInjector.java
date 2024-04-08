package com.complyt.business.strategy.transaction_rates_injection;

import com.complyt.domain.sales_tax.ComplytInternalRates;
import com.complyt.domain.transaction.Transaction;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface RatesTransactionInjector {
    Function<ComplytInternalRates, Mono<Transaction>> inject(Transaction transaction);
}
