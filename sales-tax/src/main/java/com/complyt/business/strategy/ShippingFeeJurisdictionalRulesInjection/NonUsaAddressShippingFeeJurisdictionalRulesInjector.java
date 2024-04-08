package com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection;

import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class NonUsaAddressShippingFeeJurisdictionalRulesInjector implements ShippingFeeJurisdictionalInjector {
    @Override
    public Function<Map<String, ProductClassification>, Transaction> inject(Transaction transaction) {
        return mapTaxCodesToClassifications -> {
            String country = transaction.getShippingAddress().country();
            ProductClassification classification = mapTaxCodesToClassifications.get(transaction.getShippingFee().getTaxCode());
            JurisdictionalTaxRules rules = classification.getJurisdictionalTaxRules().get(country);

            ShippingFee modifiedShippingFee = transaction.getShippingFee().withJurisdictionalTaxRules(rules);

            TaxableCategory category = modifiedShippingFee.getJurisdictionalTaxRules().isTaxable() ?
                    TaxableCategory.TAXABLE : TaxableCategory.NOT_TAXABLE;
            ShippingFee shippingFeeWithTaxableCategory = modifiedShippingFee.withTaxableCategory(category);

            log.debug("Inserting new shipping fee with rules : " + rules + ", with taxable category : " + category);

            return transaction.withShippingFee(shippingFeeWithTaxableCategory);
        };
    }
}
