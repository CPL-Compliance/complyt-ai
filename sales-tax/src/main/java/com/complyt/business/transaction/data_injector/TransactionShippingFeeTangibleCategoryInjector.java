package com.complyt.business.transaction.data_injector;

import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.enums.TangibleCategory;
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
public class TransactionShippingFeeTangibleCategoryInjector implements TransactionDataInjector<Map<String, ProductClassification>> {

    @NonNull
    private final Transaction transaction;

    @Override
    public Mono<Transaction> inject(Map<String, ProductClassification> mapTaxCodesToClassifications) {
        if (!shouldInject(mapTaxCodesToClassifications)) {
            return Mono.just(transaction);
        }

        return Mono.fromCallable(() -> {
            ProductClassification productClassification = mapTaxCodesToClassifications.get(transaction.getShippingFee().getTaxCode());
            TangibleCategory category = productClassification.getTangibleCategory();
            ShippingFee shippingFee = transaction.getShippingFee().withTangibleCategory(category);
            log.debug("Inserting new shipping fee with tangible category : " + category);

            return transaction.withShippingFee(shippingFee);
        });
    }

    @Override
    public boolean shouldInject(Map<String, ProductClassification> mapTaxCodesToClassifications) {
        if (transaction.getShippingFee() == null) {
            log.debug("Transaction doesn't have shipping fee");
            return false;
        }
        if (!mapTaxCodesToClassifications.containsKey(transaction.getShippingFee().getTaxCode())) {
            log.debug("Shipping fee's tax code does not exist in given classifications list - not injecting jurisdictional rules to it");
            return false;
        }

        return true;
    }
}


