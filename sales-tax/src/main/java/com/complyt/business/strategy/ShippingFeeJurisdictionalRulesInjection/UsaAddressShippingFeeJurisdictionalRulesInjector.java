package com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection;

import com.complyt.business.strategy.UsaAddressCityExtractor;
import com.complyt.business.strategy.items_jurisdictional_rules_injection.CityAligner;
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

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
@Slf4j
public class UsaAddressShippingFeeJurisdictionalRulesInjector implements ShippingFeeJurisdictionalInjector, UsaAddressCityExtractor {
    @Override
    public Function<Map<String, ProductClassification>, Transaction> inject(Transaction transaction) {
        return mapTaxCodesToClassifications -> {
            String state = Optional.ofNullable(transaction.getShippingAddress())
                    .map(ShippingAddress::matchedAddressData)
                    .map(MatchedAddressData::address)
                    .map(MandatoryAddress::state)
                    .orElse(null);

            if(state == null){
                ContextLogger.observeCtx("State provided or in the matchedAddress was not found in statesToStandartizedState or null", log::error);
                throw new StateNotValidatedApiException();
            }

            String alignedMatchedAddressState = StateExistsChecker.check(state);

            ProductClassification classification = mapTaxCodesToClassifications.get(transaction.getShippingFee().getTaxCode());
            JurisdictionalSalesTaxRules rules = classification.getJurisdictionalSalesTaxRules().get(alignedMatchedAddressState);

            if (rules == null) {
                ContextLogger.observeCtx("State provided or in the matchedAddress was not found in the jurisdictional sale tax rule", log::error);
                throw new StateNotFoundInJurisdictionalTaxRulesApiException();
            }

            String matchedAddressCity = Optional.ofNullable(transaction.getShippingAddress())
                    .map(ShippingAddress::matchedAddressData)
                    .map(MatchedAddressData::address)
                    .map(MandatoryAddress::city)
                    .orElse(null);

            String city = CityAligner.getCityValue(matchedAddressCity);
            rules = extractCityIfExists(rules, city);

            ShippingFee modifiedShippingFee = transaction.getShippingFee().withJurisdictionalSalesTaxRules(rules);

            TaxableCategory category = modifiedShippingFee.getJurisdictionalSalesTaxRules().isTaxable() ?
                    TaxableCategory.TAXABLE : TaxableCategory.NOT_TAXABLE;
            ShippingFee shippingFeeWithTaxableCategory = modifiedShippingFee.withTaxableCategory(category);

            log.debug("Inserting new shipping fee with rules : " + rules + ", with taxable category : " + category);

            return transaction.withShippingFee(shippingFeeWithTaxableCategory);
        };
    }
}
