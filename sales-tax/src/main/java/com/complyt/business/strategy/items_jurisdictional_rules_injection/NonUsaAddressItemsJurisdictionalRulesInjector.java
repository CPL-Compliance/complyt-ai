package com.complyt.business.strategy.items_jurisdictional_rules_injection;

import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class NonUsaAddressItemsJurisdictionalRulesInjector implements ItemsJurisdictionalInjector {

    @Override
    public Function<Map<String, ProductClassification>, List<Item>> inject(Transaction transaction) {
        return mapTaxCodesToClassifications -> {
            String jurisdiction = transaction.getShippingAddress().country();
            List<Item> modifiedItems = new ArrayList<>();

            for (Item item : transaction.getItems()) {
                ProductClassification classification = mapTaxCodesToClassifications.get(item.getTaxCode());
                JurisdictionalTaxRules rules = classification.getJurisdictionalTaxRules().get(jurisdiction);
                Item itemWithRules = item.withJurisdictionalTaxRules(rules);

                TaxableCategory category = itemWithRules.getJurisdictionalTaxRules().isTaxable() ?
                        TaxableCategory.TAXABLE : TaxableCategory.NOT_TAXABLE;
                Item itemWithCategory = itemWithRules.withTaxableCategory(category);

                log.debug("Inserting new item with rules : " + rules + ", with taxable category : " + category);
                modifiedItems.add(itemWithCategory);
            }
            return modifiedItems;
        };
    }
}
