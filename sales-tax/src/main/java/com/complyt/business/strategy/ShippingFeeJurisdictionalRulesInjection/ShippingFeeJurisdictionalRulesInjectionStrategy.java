package com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection;

import com.complyt.business.strategy.FunctionSelectorByAddressStrategy;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@AllArgsConstructor
@EqualsAndHashCode
public class ShippingFeeJurisdictionalRulesInjectionStrategy extends FunctionSelectorByAddressStrategy {

    @NonNull
    ShippingFeeJurisdictionalInjector usaAddressShippingFeeJurisdictionalRulesInjector;

    @NonNull
    ShippingFeeJurisdictionalInjector nonUsaAddressShippingFeeJurisdictionalRulesInjector;

    @Override
    public Function<Map<String, ProductClassification>, Transaction> getFunctionForUsaOption(Transaction transaction) {
        return (mapTaxCodesToClassifications) -> usaAddressShippingFeeJurisdictionalRulesInjector.inject(transaction).apply(mapTaxCodesToClassifications);
    }

    @Override
    public Function<Map<String, ProductClassification>, Transaction> getFunctionForNonUsaOption(Transaction transaction) {
        return (mapTaxCodesToClassifications) -> nonUsaAddressShippingFeeJurisdictionalRulesInjector.inject(transaction).apply(mapTaxCodesToClassifications);
    }
}
