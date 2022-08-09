package com.complyt.business.transaction;

import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Slf4j
public class TransactionProductClassificationInjector implements TransactionDataInjector<ProductClassification> {
    @NonNull
    private final Transaction transaction;

    /** finds each of the transaction's item's  jurisdictional Sales Tax Rules and injects it to the item
     * @param mapTaxCodesToClassifications - hashmap of tax code to its product classification representation
     * @return - transaction with items with the corresponding jurisdictional Sales Tax Rules in each one of them
     */
    @Override
    public Mono<Transaction> act(Map<String, ProductClassification> mapTaxCodesToClassifications) {
        return Mono.fromCallable(() -> {
            log.info("Setting jurisdictional sales tax rules to transaction's items");
            String state = transaction.getShippingAddress().getState();
            List<Item> itemsWithRules = new ArrayList<>();
            Item itemWithRules;
            for (Item item : transaction.getItems()) {
                ProductClassification productClassification = mapTaxCodesToClassifications.get(item.getTaxCode());
                JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = productClassification.getJurisdictionalSalesTaxRules().get(state);
                itemWithRules = item.withJurisdictionalSalesTaxRules(jurisdictionalSalesTaxRules);
                log.debug("Inserting new item with rules : " + itemWithRules);
                itemsWithRules.add(itemWithRules);
            }

            return transaction.withItems(itemsWithRules);
        });
    }
}
