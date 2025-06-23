package com.complyt.business.strategy.items_jurisdictional_rules_injection;

import com.complyt.business.strategy.UsaAddressCityExtractor;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.*;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.StateNotFoundInJurisdictionalTaxRulesApiException;
import com.complyt.v1.exceptions.types.StateNotValidatedApiException;
import com.complyt.v1.validators.body_checkers.StateExistsChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
@Slf4j
public class UsaAddressItemsJurisdictionalRulesInjector implements ItemsJurisdictionalInjector, UsaAddressCityExtractor {
    @Override
    public Function<Map<String, ProductClassification>, List<Item>> inject(Transaction transaction) {
        return mapTaxCodesToClassifications -> {
            String jurisdiction = Optional.ofNullable(transaction.getShippingAddress()) // Needs to create a mapper between addressValidation states to salesTax states
                    .map(ShippingAddress::matchedAddressData)
                    .map(MatchedAddressData::address)
                    .map(MandatoryAddress::state)
                    .orElse(null);

            if(jurisdiction == null){
                log.error("State provided or in the matchedAddress was not found in statesToStandartizedState or null");
                throw new StateNotValidatedApiException();
            }

            String alignedMatchedAddressJurisdiction = StateExistsChecker.check(jurisdiction);

            List<Item> modifiedItems = new ArrayList<>();

            for (Item item : transaction.getItems()) {
                ProductClassification classification = mapTaxCodesToClassifications.get(item.getTaxCode());
                JurisdictionalSalesTaxRules rules = classification.getJurisdictionalSalesTaxRules().get(alignedMatchedAddressJurisdiction);

                if (rules == null) {
                    log.error("State provided was not found in the jurisdictional sales tax rule");
                    throw new StateNotFoundInJurisdictionalTaxRulesApiException();
                }

                String matchedAddressCity = Optional.ofNullable(transaction.getShippingAddress())
                        .map(ShippingAddress::matchedAddressData)
                        .map(MatchedAddressData::address)
                        .map(MandatoryAddress::city)
                        .orElse(null);

                String city = CityAligner.getCityValue(matchedAddressCity);
                rules = extractCityIfExists(rules, city);

                Item itemWithRules = item.withJurisdictionalSalesTaxRules(rules);

                ContextLogger.observeCtx("Fetching jurisdictionalRules from product classification", log::info);
                TaxableCategory category = itemWithRules.getJurisdictionalSalesTaxRules().isTaxable() ||
                        itemWithRules.getJurisdictionalSalesTaxRules().getCities() != null &&
                                // if cities exist there is no case of null pointer exception here because of the call to extractCityIfExists
                                itemWithRules.getJurisdictionalSalesTaxRules().getCities().get(city).isTaxable() ?
                        TaxableCategory.TAXABLE : TaxableCategory.NOT_TAXABLE;

                Item itemWithCategory = itemWithRules.withTaxableCategory(category);

                ContextLogger.observeCtx("Inserting new item with rules : " + rules + ", with taxable category : " + category, log::debug);
                modifiedItems.add(itemWithCategory);
            }
            return modifiedItems;
        };
    }
}
