package com.complyt.business.strategy.transaction_rates_injection;

import com.complyt.annotations.Generated;
import com.complyt.business.strategy.FunctionSelectorByAddressStrategy;
import com.complyt.domain.sales_tax.ComplytInternalRates;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@AllArgsConstructor
@Generated
public class TransactionRatesInjectionStrategy extends FunctionSelectorByAddressStrategy {

    @NonNull
    RatesTransactionInjector complytSalesTaxRatesTransactionInjector;

    @NonNull
    RatesTransactionInjector gtRatesTransactionInjector;

    @Override
    protected Function<ComplytInternalRates, Mono<Transaction>> getFunctionForUsaOption(Transaction transaction) {
        return (complytInternalRates) -> complytSalesTaxRatesTransactionInjector.inject(transaction).apply(complytInternalRates);
    }

    @Generated
    @Override
    protected Function<ComplytInternalRates, Mono<Transaction>> getFunctionForNonUsaOption(Transaction transaction) {
        return (complytInternalRates) ->  gtRatesTransactionInjector.inject(transaction).apply(complytInternalRates);
    }
}