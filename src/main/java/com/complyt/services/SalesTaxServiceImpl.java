package com.complyt.services;

import com.complyt.business.sales_tax.SalesTaxApplyCheck;
import com.complyt.business.sales_tax.SalesTaxCalculator;
import com.complyt.business.sales_tax.SalesTaxRateCalculator;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SalesTaxServiceImpl implements SalesTaxService {

    @NonNull
    private SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @NonNull
    private SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRate;

    @NonNull
    private SalesTaxCalculator salesTaxCalculator;

    @NonNull
    private SalesTaxRateCalculator salesTaxRateCalculator;

    @NonNull
    private SalesTaxApplyCheck salesTaxApplyCheck;

    @Override
    public Mono<Transaction> handleSalesTaxCalculation(@NonNull Transaction transaction, @NonNull SalesTaxTracking salesTaxTracking) {
        return salesTaxApplyCheck.isApplied(transaction, salesTaxTracking)
                .flatMap(isApplied -> isApplied ? calculate(transaction) : Mono.just(transaction));
    }

    @Override
    public Mono<Transaction> calculate(@NonNull Transaction transaction) {
        return salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())
                .map(salesTaxDataToSalesTaxRate::map)
                .map(injectSalesTaxToTransaction(transaction));
    }

    private Function<SalesTaxRate, Transaction> injectSalesTaxToTransaction(Transaction transaction) {
        return salesTaxRate -> {
            log.info("Setting sales tax rates for transaction's items");
            List<Item> itemsWithRates = setSalesTaxRatesForItems(transaction.getItems(), salesTaxRate);
            Transaction transactionWithItemsWithRates = transaction.withItems(itemsWithRates);

            log.info("Calculating total sales tax amount for transaction");

            float salesTaxAmount = salesTaxCalculator.calculate(transactionWithItemsWithRates.getItems());
            SalesTax salesTax = new SalesTax(salesTaxAmount, salesTaxRate);

            log.debug("Transaction's sales tax : " + salesTax);
            return transactionWithItemsWithRates.withSalesTax(salesTax);
        };
    }

    private List<Item> setSalesTaxRatesForItems(List<Item> items, SalesTaxRate salesTaxRate) {
        return items.stream()
                .map(item -> item.withSalesTaxRate(salesTaxRateCalculator.calculateSalesTaxRate(item.getJurisdictionalSalesTaxRules(), salesTaxRate)))
                .collect(Collectors.toList());
    }
}
