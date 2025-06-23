package com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection;

import com.complyt.business.address.CountryToStandardizedCountry;
import com.complyt.business.strategy.NonUsaAddressRegionExtractor;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.*;
import com.complyt.v1.exceptions.types.CountryNotFoundInJurisdictionalTaxRulesApiException;
import com.complyt.v1.exceptions.types.CountryNotValidatedApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
@Slf4j
public class NonUsaAddressShippingFeeJurisdictionalRulesInjector implements ShippingFeeJurisdictionalInjector, NonUsaAddressRegionExtractor {
    @Override
    public Function<Map<String, ProductClassification>, Transaction> inject(Transaction transaction) {
        return mapTaxCodesToClassifications -> {
            String country = Optional.ofNullable(transaction.getShippingAddress())
                    .map(ShippingAddress::matchedAddressData)
                    .map(MatchedAddressData::address)
                    .map(MandatoryAddress::country)
                    .orElse(null);

            if(country == null){
                log.error("Country provided or in the matchedAddress was not found in the nonUsaCountriesAbbreviations or is 'null'");
                throw new CountryNotValidatedApiException();
            }

            String alignedMatchedAddressCountry = CountryToStandardizedCountry.standardize(country);

            ProductClassification classification = mapTaxCodesToClassifications.get(transaction.getShippingFee().getTaxCode());
            JurisdictionalTaxRules rules = classification.getJurisdictionalTaxRules().get(alignedMatchedAddressCountry);

            if (rules == null) {
                log.error("Country provided or in the matchedAddress was not found in the jurisdictional tax rule");
                throw new CountryNotFoundInJurisdictionalTaxRulesApiException();
            }

            String matchedAddressRegion = Optional.ofNullable(transaction.getShippingAddress())
                    .map(ShippingAddress::matchedAddressData)
                    .map(MatchedAddressData::address)
                    .map(MandatoryAddress::region)
                    .orElse(null);

            rules = extractRegionIfExists(rules, matchedAddressRegion);

            ShippingFee modifiedShippingFee = transaction.getShippingFee().withJurisdictionalTaxRules(rules);

            TaxableCategory category = modifiedShippingFee.getJurisdictionalTaxRules().isTaxable() ?
                    TaxableCategory.TAXABLE : TaxableCategory.NOT_TAXABLE;
            ShippingFee shippingFeeWithTaxableCategory = modifiedShippingFee.withTaxableCategory(category);

            log.debug("Inserting new shipping fee with rules : " + rules + ", with taxable category : " + category);

            return transaction.withShippingFee(shippingFeeWithTaxableCategory);
        };
    }
}
