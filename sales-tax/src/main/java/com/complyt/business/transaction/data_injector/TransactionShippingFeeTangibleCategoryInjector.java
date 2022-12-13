package com.complyt.business.transaction.data_injector;

import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;


@EqualsAndHashCode
@Getter
@Slf4j
public class TransactionShippingFeeTangibleCategoryInjector extends TransactionShippingFeeCheckToInjector {

    public TransactionShippingFeeTangibleCategoryInjector(Transaction transaction) {
        super(transaction);
    }

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
}


