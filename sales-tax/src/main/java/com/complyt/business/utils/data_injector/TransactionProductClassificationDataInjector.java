package com.complyt.business.utils.data_injector;

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
public class TransactionProductClassificationDataInjector implements TransactionDataInjector<Map<String, ProductClassification>> {

    @NonNull
    private final Transaction transaction;

    @Override
    public Mono<Transaction> inject(Map<String, ProductClassification> mapTaxCodesToClassifications) {
        return new TransactionJurisdictionalRulesInjector(transaction)
                .inject(mapTaxCodesToClassifications)
                .map(transactionWithRules -> new TransactionTangibleCategoryInjector(transactionWithRules))
                .flatMap(transactionTangibleCategoryInjector -> transactionTangibleCategoryInjector.inject(mapTaxCodesToClassifications));
    }
}
