package com.complyt.business.strategy.transaction_rates_injection;

import com.complyt.annotations.Generated;
import com.complyt.business.strategy.FunctionSelectorByTransactionAddressStrategy;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@AllArgsConstructor
@Generated
public class TransactionRatesInjectionStrategy extends FunctionSelectorByTransactionAddressStrategy {

    @NonNull
    RatesTransactionInjector<Pair<ComplytSalesTaxRates, Boolean>> complytSalesTaxRatesTransactionInjector;

    @NonNull
    RatesTransactionInjector<Pair<ComplytGtRates, Boolean>> gtRatesTransactionInjector;

    @Override
    protected Function<Pair<ComplytSalesTaxRates, Boolean>, Mono<Transaction>> getFunctionForUsaOption(Transaction transaction) {
        return Pair -> complytSalesTaxRatesTransactionInjector.inject(transaction).apply(Pair);
    }

    @Generated
    @Override
    protected Function<Pair<ComplytGtRates, Boolean>, Mono<Transaction>> getFunctionForNonUsaOption(Transaction transaction) {
        return Pair ->  gtRatesTransactionInjector.inject(transaction).apply(Pair);
    }
}