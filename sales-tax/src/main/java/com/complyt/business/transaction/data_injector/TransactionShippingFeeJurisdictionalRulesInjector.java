package com.complyt.business.transaction.data_injector;

import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;


@EqualsAndHashCode(callSuper = true)
@Getter
@Slf4j
public class TransactionShippingFeeJurisdictionalRulesInjector extends TransactionShippingFeeInjectionChecker {

    public TransactionShippingFeeJurisdictionalRulesInjector(Transaction transaction) {
        super(transaction);
    }

    @Override
    public Mono<Transaction> inject(Map<String, ProductClassification> mapTaxCodesToClassifications) {
        if (!shouldInject(mapTaxCodesToClassifications)) {
            return Mono.just(transaction);
        }

        return Mono.fromCallable(() -> {
            String state = transaction.getShippingAddress().getState();
            ProductClassification classification = mapTaxCodesToClassifications.get(transaction.getShippingFee().getTaxCode());
            JurisdictionalSalesTaxRules rules = classification.getJurisdictionalSalesTaxRules().get(state);

            ShippingFee modifiedShippingFee = transaction.getShippingFee().withJurisdictionalSalesTaxRules(rules);

            TaxableCategory category = modifiedShippingFee.getJurisdictionalSalesTaxRules().taxable() ?
                    TaxableCategory.TAXABLE : TaxableCategory.NOT_TAXABLE;
            ShippingFee shippingFeeWithTaxableCategory = modifiedShippingFee.withTaxableCategory(category);

            log.debug("Inserting new shipping fee with rules : " + rules + ", with taxable category : " + category);

            return transaction.withShippingFee(shippingFeeWithTaxableCategory);
        });
    }
}
