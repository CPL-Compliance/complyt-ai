package com.complyt.business.data_injector;

import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
        if (transaction.getShippingFee() == null || !mapTaxCodesToClassifications.containsKey(transaction.getShippingFee().getTaxCode())) {
            log.debug("No jurisdictional rules to inject to shipping fee");
            return Mono.just(transaction);
        }

        return Mono.fromCallable(() -> {
            String state = transaction.getShippingAddress().getState();
            ProductClassification classification = mapTaxCodesToClassifications.get(transaction.getShippingFee().getTaxCode());
            JurisdictionalSalesTaxRules rules = classification.getJurisdictionalSalesTaxRules().get(state);
            log.debug("Inserting Shipping fee with rules : " + rules);

            ShippingFee modifiedShippingFee = transaction.getShippingFee().withJurisdictionalSalesTaxRules(rules);
            return transaction.withShippingFee(modifiedShippingFee);
        });
    }
}
