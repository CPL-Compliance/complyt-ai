package com.complyt.business.data_injector;

import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Slf4j
public class TransactionJurisdictionalRulesInjector implements TransactionDataInjector<Map<String, ProductClassification>> {

    @NonNull
    private final Transaction transaction;

    /** finds each of the transaction's item's  jurisdictional Sales Tax Rules and injects it to the item
     * @param mapTaxCodesToClassifications - hashmap of tax code to its product classification representation
     * @return - transaction with items with the corresponding jurisdictional Sales Tax Rules in each one of them
     */
    @Override
    public Mono<Transaction> inject(Map<String, ProductClassification> mapTaxCodesToClassifications) {
        return Mono.fromCallable(() -> {
            log.info("Setting jurisdictional sales tax rules and taxable categories to transaction's items");
            List<Item> modifiedItems = createItemsWithRules(mapTaxCodesToClassifications);
            Transaction modifiedTransaction = transaction.withItems(modifiedItems);
            log.debug("Transaction with items with rules and taxable categories injected : " + modifiedTransaction);

            return modifiedTransaction;
        });
    }

    private List<Item> createItemsWithRules(Map<String, ProductClassification> mapTaxCodesToClassifications) {
        String state = transaction.getShippingAddress().getState();
        List<Item> modifiedItems = new ArrayList<>();

        for (Item item : transaction.getItems()) {
            ProductClassification classification = mapTaxCodesToClassifications.get(item.getTaxCode());

            JurisdictionalSalesTaxRules rules = classification.getJurisdictionalSalesTaxRules().get(state);
            Item itemWithRules = item.withJurisdictionalSalesTaxRules(rules);

            TaxableCategory category  = itemWithRules.getJurisdictionalSalesTaxRules().isTaxable() ?
                    TaxableCategory.TAXABLE : TaxableCategory.NOT_TAXABLE;
            Item itemWithCategory = itemWithRules.withTaxableCategory(category);

            log.debug("Inserting new item with rules : " + rules + ", with taxable category : " + category);
            modifiedItems.add(itemWithCategory);
        }

        return modifiedItems;
    }

}