package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.SalesTaxRate;
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

    public List<Item> setSalesTaxRates(List<Item> items, SalesTaxRate salesTaxRate, Address address) {
        return items.stream()
                .map(item -> item.withSalesTaxRate(salesTaxRatesProvider.provide(item.getJurisdictionalSalesTaxRules(), salesTaxRate, address)))
                .collect(Collectors.toList());
    }
}