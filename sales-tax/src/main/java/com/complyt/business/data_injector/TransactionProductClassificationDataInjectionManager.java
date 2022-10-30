package com.complyt.business.data_injector;

import com.complyt.domain.Transaction;
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
                .map(transactionWithItemsWithRules -> new TransactionItemsTangibleCategoryInjector(transactionWithItemsWithRules))
                .flatMap(transactionTangibleCategoryInjector -> transactionTangibleCategoryInjector.inject(mapTaxCodesToClassifications))
                .map(transactionWithModifiedItems -> new TransactionShippingFeeJurisdictionalRulesInjector(transactionWithModifiedItems))
                .flatMap(transactionShippingFeeJurisdictionalRulesInjector -> transactionShippingFeeJurisdictionalRulesInjector.inject(mapTaxCodesToClassifications))
                .map(transactionWithModifiedItemsAndShippingFeeWithRules -> new TransactionShippingFeeTangibleCategoryInjector(transactionWithModifiedItemsAndShippingFeeWithRules))
                .flatMap(transactionShippingFeeTangibleCategoryInjector -> transactionShippingFeeTangibleCategoryInjector.inject(mapTaxCodesToClassifications));
    }
}