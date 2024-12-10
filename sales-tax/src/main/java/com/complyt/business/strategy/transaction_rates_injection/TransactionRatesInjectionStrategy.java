package com.complyt.business.strategy.transaction_rates_injection;

import com.complyt.annotations.Generated;
import com.complyt.business.strategy.FunctionSelectorByTransactionAddressStrategy;
import com.complyt.domain.sales_tax.ComplytInternalRates;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@AllArgsConstructor
@Generated
public class TransactionRatesInjectionStrategy extends FunctionSelectorByTransactionAddressStrategy {

    @NonNull
    RatesTransactionInjector<ComplytSalesTaxRates> complytSalesTaxRatesTransactionInjector;

    @NonNull
    RatesTransactionInjector<ComplytGtRates> gtRatesTransactionInjector;

    @Override
    protected Function<ComplytSalesTaxRates, Mono<Transaction>> getFunctionForUsaOption(Transaction transaction) {
        return ComplytSalesTaxRates -> complytSalesTaxRatesTransactionInjector.inject(transaction).apply(ComplytSalesTaxRates);
    }

    @Generated
    @Override
    protected Function<ComplytGtRates, Mono<Transaction>> getFunctionForNonUsaOption(Transaction transaction) {
        return ComplytGtRates ->  gtRatesTransactionInjector.inject(transaction).apply(ComplytGtRates);
    }
}