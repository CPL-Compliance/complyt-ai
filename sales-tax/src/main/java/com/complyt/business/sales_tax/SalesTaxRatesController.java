package com.complyt.business.sales_tax;

import com.complyt.domain.Item;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.SalesTaxRate;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class SalesTaxRatesController {

    @NonNull
    private SalesTaxRateCalculator salesTaxRateCalculator;

    public Transaction setRates(@NonNull Transaction transaction, @NonNull SalesTaxRate salesTaxRate) {
        List<Item> itemsWithRates = setSalesTaxRatesForItems(transaction.getItems(), salesTaxRate);

        if (transaction.getShippingFee() != null) {
            ShippingFee shippingFee = setSalesTaxRateForShippingFee(transaction.getShippingFee(), salesTaxRate);
            return transaction.withItems(itemsWithRates).withShippingFee(shippingFee);
        }

        return transaction.withItems(itemsWithRates);
    }

    private List<Item> setSalesTaxRatesForItems(List<Item> items, SalesTaxRate salesTaxRate) {
        return items.stream()
                .map(item -> item.withSalesTaxRate(salesTaxRateCalculator.calculateSalesTaxRate(item.getJurisdictionalSalesTaxRules(), salesTaxRate)))
                .collect(Collectors.toList());
    }

    private ShippingFee setSalesTaxRateForShippingFee(@NonNull ShippingFee shippingFee, SalesTaxRate salesTaxRate) {
        SalesTaxRate shippingFeeSalesTaxRate = salesTaxRateCalculator.calculateSalesTaxRate(shippingFee.getJurisdictionalSalesTaxRules(), salesTaxRate);
        return shippingFee.withSalesTaxRate(shippingFeeSalesTaxRate);
    }
}