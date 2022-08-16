package com.complyt.business.utils.data_injector;

import com.complyt.business.sales_tax.SalesTaxCalculator;
import com.complyt.business.sales_tax.SalesTaxRateCalculator;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class TransactionSalesTaxInjector implements TransactionDataInjector<Pair<Transaction, SalesTaxRate>> {

    @NonNull
    private SalesTaxCalculator salesTaxCalculator;

    @NonNull
    private SalesTaxRateCalculator salesTaxRateCalculator;

    public Mono<Transaction> inject(@NonNull Pair<Transaction, SalesTaxRate> transactionSalesTaxRatePair) {
        return Mono.fromCallable(() -> {
            Transaction transaction = transactionSalesTaxRatePair.getValue0();
            SalesTaxRate salesTaxRate = transactionSalesTaxRatePair.getValue1();

            log.info("Setting sales tax rates for transaction's items");
            List<Item> itemsWithRates = setSalesTaxRatesForItems(transaction.getItems(), salesTaxRate);
            Transaction transactionWithItemsWithRates = transaction.withItems(itemsWithRates);

            log.info("Calculating total sales tax amount for transaction");

            float salesTaxAmount = salesTaxCalculator.calculate(transactionWithItemsWithRates.getItems());
            SalesTax salesTax = new SalesTax(salesTaxAmount, salesTaxRate);

            log.debug("Transaction's sales tax : " + salesTax);
            return transactionWithItemsWithRates.withSalesTax(salesTax);
        });
    }

    private List<Item> setSalesTaxRatesForItems(List<Item> items, SalesTaxRate salesTaxRate) {
        return items.stream()
                .map(item -> item.withSalesTaxRate(salesTaxRateCalculator.calculateSalesTaxRate(item.getJurisdictionalSalesTaxRules(), salesTaxRate)))
                .collect(Collectors.toList());
    }
}
