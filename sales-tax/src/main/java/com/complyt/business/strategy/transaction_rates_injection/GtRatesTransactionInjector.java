package com.complyt.business.strategy.transaction_rates_injection;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.tax.gt.TransactionGtRatesHandler;
import com.complyt.business.tax.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.domain.Taxable;
import com.complyt.domain.sales_tax.ComplytInternalRates;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class GtRatesTransactionInjector implements RatesTransactionInjector<ComplytGtRates> {
    @NonNull
    private TransactionGtRatesHandler transactionGtRatesHandler;

    @NonNull
    private CollectionBuilder<Taxable> taxableCollectionBuilder;

    @NonNull
    private SalesTaxAggregator salesTaxAggregator;

    @Override
    public Function<ComplytGtRates, Mono<Transaction>> inject(Transaction transaction) {
        return complytGtRates -> Mono.just(complytGtRates.gtRates())
                .flatMap(gtRates -> transactionGtRatesHandler.setRates(transaction, gtRates)
                        .map(transactionWithRates -> {
                            List<Taxable> taxables = (List<Taxable>) taxableCollectionBuilder.build(transactionWithRates);
                            BigDecimal salesTaxAmount = salesTaxAggregator.aggregate(taxables, transaction.getIsTaxInclusive());
                            SalesTax salesTax = new SalesTax(null, salesTaxAmount, gtRates.taxRate(), null, gtRates);

                            BigDecimal finalAmount = transaction.getIsTaxInclusive() ?
                                    transaction.getFinalTransactionAmount() :
                                    transaction.getFinalTransactionAmount().add(salesTaxAmount);

                            return transactionWithRates.setSalesTax(salesTax)
                                    .setFinalTransactionAmount(finalAmount);
                        }));
    }
}
