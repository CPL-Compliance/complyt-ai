package com.complyt.business.transaction.data_injector;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Getter
@Component
@Slf4j
public class TransactionShippingFeeTangibleCategoryInjector extends TransactionShippingFeeInjectionChecker {

    public TransactionShippingFeeTangibleCategoryInjector() {
        super();
    }

    @Override
    public Mono<Transaction> inject(Map<String, ProductClassification> mapTaxCodesToClassifications, @NonNull Transaction transaction) {
        if (!shouldInject(mapTaxCodesToClassifications, transaction)) {
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


