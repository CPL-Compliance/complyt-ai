package com.complyt.business.transaction.data_injector;

import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;

@Getter
@AllArgsConstructor
@Slf4j
public class TransactionProductClassificationDataInjectionManager implements TransactionDataInjector<Map<String, ProductClassification>> {

    @NonNull
    private final Transaction transaction;

    @Override
    public Mono<Transaction> inject(Map<String, ProductClassification> mapTaxCodesToClassifications) {
        return new TransactionItemsJurisdictionalRulesInjector(transaction).inject(mapTaxCodesToClassifications)
                .map(TransactionItemsTangibleCategoryInjector::new)
                .flatMap(transactionTangibleCategoryInjector -> transactionTangibleCategoryInjector.inject(mapTaxCodesToClassifications))
                .map(TransactionShippingFeeJurisdictionalRulesInjector::new)
                .flatMap(transactionShippingFeeJurisdictionalRulesInjector -> transactionShippingFeeJurisdictionalRulesInjector.inject(mapTaxCodesToClassifications))
                .map(TransactionShippingFeeTangibleCategoryInjector::new)
                .flatMap(transactionShippingFeeTangibleCategoryInjector -> transactionShippingFeeTangibleCategoryInjector.inject(mapTaxCodesToClassifications));
    }
}