package com.complyt.business.tax.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.Item;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class ItemsSalesTaxRatesProvider implements TaxableSalesTaxRatesProvider<List<Item>> {

    @NonNull
    private SalesTaxRatesProvider salesTaxRatesProvider;

    public List<Item> setSalesTaxRates(List<Item> items, SalesTaxRates salesTaxRates, Address address) {
        return items.stream()
                .map(item -> item.withSalesTaxRates(salesTaxRatesProvider.provide(item.getJurisdictionalSalesTaxRules(), salesTaxRates, address)))
                .collect(Collectors.toList());
    }
}