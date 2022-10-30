package com.complyt.business.data_injector;

import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Slf4j
public class TransactionShippingFeeJurisdictionalRulesInjector implements TransactionDataInjector<Map<String, ProductClassification>> {

    @NonNull
    private final Transaction transaction;

    @Override
    public Mono<Transaction> inject(Map<String, ProductClassification> mapTaxCodesToClassifications) {
        if (transaction.getShippingFee() == null) {
            log.debug("Transaction doesn't have shipping fee");
            return Mono.just(transaction);
        }
        if (!mapTaxCodesToClassifications.containsKey(transaction.getShippingFee().getTaxCode())) {
            log.debug("Shipping fee's tax code does not exist in given classifications list - not injecting jurisdictional rules to it");
            return Mono.just(transaction);
        }

        return Mono.fromCallable(() -> {
            String state = transaction.getShippingAddress().getState();
            ProductClassification classification = mapTaxCodesToClassifications.get(transaction.getShippingFee().getTaxCode());
            JurisdictionalSalesTaxRules rules = classification.getJurisdictionalSalesTaxRules().get(state);

            ShippingFee modifiedShippingFee = transaction.getShippingFee().withJurisdictionalSalesTaxRules(rules);

            TaxableCategory category = modifiedShippingFee.getJurisdictionalSalesTaxRules().isTaxable() ?
                    TaxableCategory.TAXABLE : TaxableCategory.NOT_TAXABLE;
            ShippingFee shippingFeeWithTaxableCategory = modifiedShippingFee.withTaxableCategory(category);

            log.debug("Inserting new shipping fee with rules : " + rules + ", with taxable category : " + category);

            return transaction.withShippingFee(shippingFeeWithTaxableCategory);
        });
    }
}
