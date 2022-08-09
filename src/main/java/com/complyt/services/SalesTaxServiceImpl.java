package com.complyt.services;

import com.complyt.business.sales_tax.SalesTaxApplyCheck;
import com.complyt.business.sales_tax.SalesTaxCalculator;
import com.complyt.business.sales_tax.SalesTaxRateCalculator;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxData;
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
        boolean isApplied = salesTaxApplyCheck.isApplied(transaction, salesTaxTracking);

        return salesTaxTracking.isEnforcesSalesTax() && isApplied ? calculate(transaction) : Mono.just(transaction);
    }

    @Override
    public Mono<Transaction> calculate(@NonNull Transaction transaction) {
        return findByAddress(transaction.getShippingAddress())
                .map(this::salesTaxDataToSalesTaxRate)
                .map(injectSalesTaxToTransaction(transaction));
    }

    @Override
    public float calculateSalesTaxAmount(List<Item> items) {
        return salesTaxCalculator.calculate(items);
    }

    private Mono<SalesTaxData> findByAddress(Address address) {
        return salesTaxWebClientWrapper.findByAddress(address);
    }

    private SalesTaxRate salesTaxDataToSalesTaxRate(SalesTaxData salesTaxData) {
        return salesTaxDataToSalesTaxRate.map(salesTaxData);
    }

    private Function<SalesTaxRate, Transaction> injectSalesTaxToTransaction(Transaction transaction) {
        return salesTaxRate -> {
            log.info("Setting sales tax rates for transaction's items");
            List<Item> itemsWithRates = setSalesTaxRatesForItems(transaction.getItems(), salesTaxRate);
            Transaction transactionWithItemsWithRates = transaction.withItems(itemsWithRates);

            log.info("Calculating total sales tax amount for transaction");
            float salesTaxAmount = calculateSalesTaxAmount(transactionWithItemsWithRates.getItems());
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
