package com.complyt.business.strategy.items_jurisdictional_rules_injection;

import com.complyt.business.strategy.FunctionSelectorByTransactionAddressStrategy;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class ItemsJurisdictionalRulesInjectionStrategy extends FunctionSelectorByTransactionAddressStrategy {

    @NonNull
    ItemsJurisdictionalInjector usaAddressItemsJurisdictionalRulesInjector;

    @NonNull
    ItemsJurisdictionalInjector nonUsaAddressItemsJurisdictionalRulesInjector;

    @Override
    protected Function<Map<String, ProductClassification>, List<Item>> getFunctionForUsaOption(Transaction transaction) {
        return (mapTaxCodesToClassifications) -> usaAddressItemsJurisdictionalRulesInjector.inject(transaction).apply(mapTaxCodesToClassifications);
    }

    @Override
    protected Function<Map<String, ProductClassification>, List<Item>> getFunctionForNonUsaOption(Transaction transaction) {
        return (mapTaxCodesToClassifications) -> nonUsaAddressItemsJurisdictionalRulesInjector.inject(transaction).apply(mapTaxCodesToClassifications);
    }
}
