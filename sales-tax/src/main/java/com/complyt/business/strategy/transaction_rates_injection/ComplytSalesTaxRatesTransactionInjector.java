package com.complyt.business.strategy.transaction_rates_injection;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.tax.sales_tax.mapper.ComplytSalesTaxRatesToSalesTaxRates;
import com.complyt.business.tax.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.business.tax.sales_tax.sales_tax_rates.TransactionSalesTaxRatesHandler;
import com.complyt.domain.Taxable;
import com.complyt.domain.sales_tax.ComplytInternalRates;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class ComplytSalesTaxRatesTransactionInjector implements RatesTransactionInjector {
    @NonNull
    private ComplytSalesTaxRatesToSalesTaxRates complytSalesTaxRatesToSalesTaxRates;

    @NonNull
    private TransactionSalesTaxRatesHandler transactionSalesTaxRatesHandler;

    @NonNull
    private CollectionBuilder<Taxable> taxableCollectionBuilder;

    @NonNull
    private SalesTaxAggregator salesTaxAggregator;

    @Override
    public Function<ComplytInternalRates, Mono<Transaction>> inject(Transaction transaction) {
        return complytSalesTaxRates -> complytSalesTaxRatesToSalesTaxRates.map((ComplytSalesTaxRates) complytSalesTaxRates) //todo: I think this mapping is unnecesarry, we can just do complytSalesTaxRates.salesTaxRate
                .flatMap(salesTaxRates -> transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRates)
                        .map(transactionWithRates -> {
                            List<Taxable> taxables = (List<Taxable>) taxableCollectionBuilder.build(transactionWithRates);
                            BigDecimal salesTaxAmount = salesTaxAggregator.aggregate(taxables);
                            SalesTax salesTax = new SalesTax(salesTaxAmount, salesTaxRates.taxRate(), salesTaxRates, null);

                            BigDecimal finalAmount = transaction.getIsTaxInclusive() ?
                                    transaction.getFinalTransactionAmount().subtract(salesTaxAmount) :
                                    transaction.getFinalTransactionAmount();

                            return transactionWithRates.withSalesTax(salesTax)
                                    .withFinalTransactionAmount(finalAmount);
                        }));
    }
}
