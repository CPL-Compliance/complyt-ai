package com.complyt.business.strategy.items_jurisdictional_rules_injection;

import com.complyt.business.strategy.UsaAddressCityExtractor;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.StateNotFoundInJurisdictionalTaxRulesApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class UsaAddressItemsJurisdictionalRulesInjector implements ItemsJurisdictionalInjector, UsaAddressCityExtractor {
    @Override
    public Function<Map<String, ProductClassification>, List<Item>> inject(Transaction transaction) {
        return mapTaxCodesToClassifications -> {
            String jurisdiction = transaction.getShippingAddress().state();
            List<Item> modifiedItems = new ArrayList<>();

            for (Item item : transaction.getItems()) {
                ProductClassification classification = mapTaxCodesToClassifications.get(item.getTaxCode());
                JurisdictionalSalesTaxRules rules = classification.getJurisdictionalSalesTaxRules().get(jurisdiction);

                if (rules == null) {
                    ContextLogger.observeCtx("State provided was not found in the jurisdictional sales tax rule", log::error);
                    throw new StateNotFoundInJurisdictionalTaxRulesApiException();
                }

                String city = transaction.getShippingAddress().city();
                rules = extractCityIfExists(rules, city);

                Item itemWithRules = item.withJurisdictionalSalesTaxRules(rules);

                ContextLogger.observeCtx("Fetching jurisdictionalRules from product classification", log::info);
                TaxableCategory category = itemWithRules.getJurisdictionalSalesTaxRules().isTaxable() ||
                        itemWithRules.getJurisdictionalSalesTaxRules().getCities() != null &&
                                itemWithRules.getJurisdictionalSalesTaxRules().getCities().get(transaction.getShippingAddress().city()) != null &&
                                itemWithRules.getJurisdictionalSalesTaxRules().getCities().get(transaction.getShippingAddress().city()).isTaxable() ?
                        TaxableCategory.TAXABLE : TaxableCategory.NOT_TAXABLE;

                Item itemWithCategory = itemWithRules.withTaxableCategory(category);

                ContextLogger.observeCtx("Inserting new item with rules : " + rules + ", with taxable category : " + category, log::debug);
                modifiedItems.add(itemWithCategory);
            }
            return modifiedItems;
        };
    }
}
