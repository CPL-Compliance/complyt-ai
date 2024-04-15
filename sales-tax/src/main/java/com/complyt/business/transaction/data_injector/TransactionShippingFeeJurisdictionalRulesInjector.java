package com.complyt.business.transaction.data_injector;

import com.complyt.business.strategy.StrategySelector;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;


@Getter
@AllArgsConstructor
@Component
@Slf4j
public class TransactionShippingFeeJurisdictionalRulesInjector extends TransactionShippingFeeInjectionChecker {

    @NonNull
    StrategySelector shippingFeeJurisdictionalRulesInjectionStrategy;

    @Override
    public Mono<Transaction> inject(Map<String, ProductClassification> mapTaxCodesToClassifications, @NonNull Transaction transaction) {
        if (!shouldInject(mapTaxCodesToClassifications, transaction)) {
            return Mono.just(transaction);
        }

        return Mono.fromCallable(() -> (Transaction) shippingFeeJurisdictionalRulesInjectionStrategy.select(transaction).apply(mapTaxCodesToClassifications));
    }
}
