package com.complyt.business.strategy.transaction_rates_injection;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.tax.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.business.tax.sales_tax.sales_tax_rates.TransactionSalesTaxRatesHandler;
import com.complyt.business.transaction.data_injector.TransactionMatchedAddressInjector;
import com.complyt.domain.Taxable;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Component
@Slf4j
@AllArgsConstructor
public class ComplytSalesTaxRatesTransactionInjector implements RatesTransactionInjector<Pair<ComplytSalesTaxRates, Boolean>> {

    @NonNull
    private TransactionSalesTaxRatesHandler transactionSalesTaxRatesHandler;

    @NonNull
    private CollectionBuilder<Taxable> taxableCollectionBuilder;

    @NonNull
    private SalesTaxAggregator salesTaxAggregator;

    @NonNull
    private TransactionMatchedAddressInjector transactionMatchedAddressInjector;


    @Override
    public Function<Pair<ComplytSalesTaxRates, Boolean>, Mono<Transaction>> inject(Transaction transaction) {
        return pair -> {
            ComplytSalesTaxRates complytSalesTaxRates = pair.getValue0();
            Boolean isExempt = pair.getValue1();

            return setTransactionSalesTaxRates(transaction, complytSalesTaxRates)
                    .flatMap(transactionWithRates -> injectCityCountyData(transactionWithRates, complytSalesTaxRates))
                    .map(transactionWithRatesAndCounty -> calculateFinalTransactionAmounts(transactionWithRatesAndCounty, complytSalesTaxRates, isExempt));
        };
    }

    private Mono<Transaction> setTransactionSalesTaxRates(Transaction transaction, ComplytSalesTaxRates complytSalesTaxRates) {
        SalesTaxRates salesTaxRates = complytSalesTaxRates.salesTaxRates();
        return transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRates);
    }

    private Mono<Transaction> injectCityCountyData(Transaction transaction, ComplytSalesTaxRates complytSalesTaxRates) {
        return transactionMatchedAddressInjector.inject(complytSalesTaxRates.matchedAddressData(), transaction);
    }

    private Transaction calculateFinalTransactionAmounts(Transaction transaction, ComplytSalesTaxRates complytSalesTaxRates, Boolean isExempt) {
        if (isExempt) {
            ContextLogger.observeCtx("Customer with ID " + transaction.getCustomerId() + " is exempt in " + transaction.getShippingAddress().state(), log::debug);
            ContextLogger.observeCtx("Removing salesTaxRate object from the items in transaction with externalId " + transaction.getExternalId(), log::debug);
            handleItemsIfExempt(transaction);
        }

        List<Taxable> taxables = buildTaxableCollection(transaction, isExempt);
        if (taxables.isEmpty()) {
            return transaction;
        }

        BigDecimal salesTaxAmount = salesTaxAggregator.aggregate(taxables, transaction.getIsTaxInclusive());
        SalesTaxRates salesTaxRates = complytSalesTaxRates.salesTaxRates();
        UUID complytId = complytSalesTaxRates.complytId();

        SalesTax salesTax = new SalesTax(complytId, salesTaxAmount, salesTaxRates.taxRate(), salesTaxRates, null);
        BigDecimal finalAmount = transaction.getIsTaxInclusive() ?
                transaction.getFinalTransactionAmount() :
                transaction.getFinalTransactionAmount().add(salesTaxAmount);

        return transaction.setSalesTax(salesTax).setFinalTransactionAmount(finalAmount);
    }

    private void handleItemsIfExempt(Transaction transaction) {
        List<Item> items = transaction.getItems().stream()
                .map(item -> item.setSalesTaxRates(null))
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
