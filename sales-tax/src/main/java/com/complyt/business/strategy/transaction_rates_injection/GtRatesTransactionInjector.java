package com.complyt.business.strategy.transaction_rates_injection;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.tax.gt.TransactionGtRatesHandler;
import com.complyt.business.tax.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.domain.Taxable;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@Component
@Slf4j
@AllArgsConstructor
public class GtRatesTransactionInjector implements RatesTransactionInjector<Pair<ComplytGtRates, Boolean>> {
    @NonNull
    private TransactionGtRatesHandler transactionGtRatesHandler;

    @NonNull
    private CollectionBuilder<Taxable> taxableCollectionBuilder;

    @NonNull
    private SalesTaxAggregator salesTaxAggregator;

    @Override
    public Function<Pair<ComplytGtRates, Boolean>, Mono<Transaction>> inject(Transaction transaction) {
        return pair -> Mono.just(pair.getValue0().gtRates())
                .flatMap(gtRates -> transactionGtRatesHandler.setRates(transaction, gtRates)
                        .map(transactionWithRates -> {
                            Boolean isExempt = pair.getValue1();
                            if (isExempt) {
                                ContextLogger.observeCtx("Customer with ID " + transactionWithRates.getCustomerId() + " is exempt in " + transactionWithRates.getShippingAddress().country(), log::debug);
                                ContextLogger.observeCtx("Removing salesTaxRate object from the items in transaction with externalId " + transactionWithRates.getExternalId(), log::debug);
                                handleItemsIfExempt(transactionWithRates);
                            }

                            List<Taxable> taxables = buildTaxableCollection(transactionWithRates, isExempt);
                            if (taxables.isEmpty()) {
                                return transactionWithRates;
                            }

                            BigDecimal salesTaxAmount = salesTaxAggregator.aggregate(taxables, transaction.getIsTaxInclusive());
                            SalesTax salesTax = new SalesTax(null, salesTaxAmount, gtRates.taxRate(), null, gtRates);

                            BigDecimal finalAmount = transaction.getIsTaxInclusive() ?
                                    transaction.getFinalTransactionAmount() :
                                    transaction.getFinalTransactionAmount().add(salesTaxAmount);

                            return transactionWithRates.setSalesTax(salesTax)
                                    .setFinalTransactionAmount(finalAmount);
                        }));
    }

    private void handleItemsIfExempt(Transaction transaction) {
        List<Item> items = transaction.getItems().stream()
                .map(item -> item.setGtRates(null))
                .toList();
        transaction.setItems(items);
    }

    private List<Taxable> buildTaxableCollection(Transaction transaction, Boolean isExempt) {
        List<Taxable> taxables = (List<Taxable>) taxableCollectionBuilder.build(transaction);
        if (isExempt) {
            taxables = taxables.stream().filter(Taxable::isManualSalesTax).toList();
        }
        return taxables;
    }
}
