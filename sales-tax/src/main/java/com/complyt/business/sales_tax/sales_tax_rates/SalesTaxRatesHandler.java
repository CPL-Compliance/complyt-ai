package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Item;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.SalesTaxRate;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class SalesTaxRatesHandler {

    @NonNull
    private ShippingFeeSalesTaxRatesCalculator shippingFeeSalesTaxRatesCalculator;

    @NonNull
    private ItemsSalesTaxRatesCalculator itemsSalesTaxRatesCalculator;

    public Transaction setRates(@NonNull Transaction transaction, @NonNull SalesTaxRate salesTaxRate) {
        log.info("Setting sales tax rates for transaction");

        List<Item> itemsWithRates = itemsSalesTaxRatesCalculator.setSalesTaxRates(transaction.getItems(), salesTaxRate);

        if (transaction.getShippingFee() != null) {
            ShippingFee shippingFeeWithRates = shippingFeeSalesTaxRatesCalculator.setSalesTaxRates(transaction.getShippingFee(), salesTaxRate);
            transaction = transaction.withShippingFee(shippingFeeWithRates);
        }

        return transaction.withItems(itemsWithRates);
    }
}