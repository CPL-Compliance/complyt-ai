package com.complyt.business.tax.gt;

import com.complyt.domain.transaction.tax.GtAddress;
import com.complyt.domain.transaction.tax.GtRates;
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
public class ItemsGtRatesProvider implements TaxableGtRatesProvider<List<Item>> {

    @NonNull
    private GtRatesProvider gtRatesProvider;

    @Override
    public List<Item> setGtRates(List<Item> items, GtRates gtRates, GtAddress gtAddress) {
        return items.stream()
                .map(item -> item.withGtRates(gtRatesProvider.provide(item.getJurisdictionalTaxRules(), gtRates, gtAddress)))
                .collect(Collectors.toList());
    }
}
