package com.complyt.business.transaction.data_injector;

import com.complyt.business.strategy.StrategySelector;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import com.complyt.utils.observability.ContextLogger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@Component
@Slf4j
public class TransactionItemsJurisdictionalRulesInjector implements TransactionDataInjector<Map<String, ProductClassification>> {

    @NonNull
    StrategySelector itemsJurisdictionalRulesInjectionStrategy;

    /**
     * finds each of the transaction's item's  jurisdictional Sales Tax Rules and injects it to the item
     *
     * @param mapTaxCodesToClassifications hashmap of tax code to its product classification representation
     * @return transaction with items with the corresponding jurisdictional Sales Tax Rules in each one of them
     */
    @Override
    public Mono<Transaction> inject(Map<String, ProductClassification> mapTaxCodesToClassifications, @NonNull Transaction transaction) {
        String stateInfoIfStateExists = transaction.getShippingAddress().state() != null ? " and in state " + transaction.getShippingAddress().state() : "";

        String logStr = "Setting jurisdictional sales tax rules and taxable categories to transaction's items for the country: "
                + transaction.getShippingAddress().country() + stateInfoIfStateExists + ", for the items: " + transaction.getItems();

        return ContextLogger.observeCtx(logStr, log::info)
                .then(Mono.just((List<Item>) itemsJurisdictionalRulesInjectionStrategy.select(transaction).apply(mapTaxCodesToClassifications)))
                .flatMap(itemsWithRules -> Mono.just(transaction.withItems(itemsWithRules)))
                .flatMap(modifiedTransaction -> ContextLogger.observeCtx("Transaction with rules and taxable categories injected : " + modifiedTransaction, log::debug)
                        .thenReturn(modifiedTransaction));
    }

}