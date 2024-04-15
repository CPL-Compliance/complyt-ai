package com.complyt.business.transaction.data_injector;

import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@AllArgsConstructor
@Component
@Slf4j
public class TransactionProductClassificationDataInjector implements TransactionDataInjector<Map<String, ProductClassification>> {


    @NonNull
    TransactionShippingFeeJurisdictionalRulesInjector transactionShippingFeeJurisdictionalRulesInjector;

    @NonNull
    TransactionShippingFeeTangibleCategoryInjector transactionShippingFeeTangibleCategoryInjector;

    @NonNull
    TransactionItemsTangibleCategoryInjector transactionItemsTangibleCategoryInjector;

    @NonNull
    TransactionItemsJurisdictionalRulesInjector transactionItemsJurisdictionalRulesInjector;


    @Override
    public Mono<Transaction> inject(Map<String, ProductClassification> mapTaxCodesToClassifications, @NonNull Transaction transaction) {
        return  transactionItemsJurisdictionalRulesInjector.inject(mapTaxCodesToClassifications,transaction)
                .flatMap(transactionWithItemsWithJurisdictionalRules -> transactionItemsTangibleCategoryInjector.inject(mapTaxCodesToClassifications,transactionWithItemsWithJurisdictionalRules))
                .flatMap(transactionWithItemsWithJurisdictionalRulesAndTangibleCategory -> transactionShippingFeeJurisdictionalRulesInjector.inject(mapTaxCodesToClassifications, transactionWithItemsWithJurisdictionalRulesAndTangibleCategory))
                .flatMap(transactionWithPCData -> transactionShippingFeeTangibleCategoryInjector.inject(mapTaxCodesToClassifications, transactionWithPCData));
    }

}