package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Item;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class TransactionSalesTaxRatesHandler {

    @NonNull
    private TaxableSalesTaxRatesProvider<ShippingFee> shippingFeeSalesTaxRatesProvider;

    @NonNull
    private TaxableSalesTaxRatesProvider<List<Item>> itemsSalesTaxRatesProvider;

    public Mono<Transaction> setRates(@NonNull Transaction transaction, @NonNull SalesTaxRates salesTaxRates) {
        List<Item> itemsWithRates = itemsSalesTaxRatesProvider.setSalesTaxRates(transaction.getItems(), salesTaxRates, transaction.getShippingAddress());

        if (transaction.getShippingFee() != null) {
            ShippingFee shippingFeeWithRates = shippingFeeSalesTaxRatesProvider.setSalesTaxRates(transaction.getShippingFee(), salesTaxRates, transaction.getShippingAddress());
            transaction = transaction.withShippingFee(shippingFeeWithRates);
        }

        return ContextLogger.observeCtx("Set Sales tax rates to Transaction", log::info)
                .then(Mono.just(transaction.withItems(itemsWithRates)));
    }

}