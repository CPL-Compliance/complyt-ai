package com.complyt.business.strategy;

import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;

import java.util.Map;

public interface UsaAddressCityExtractor {

    default JurisdictionalSalesTaxRules extractCityIfExists(JurisdictionalSalesTaxRules rules, String city) {
        if (rules.getCities() != null) {

            if (rules.getCities().get(city) != null) {
                return rules.withCities(Map.of(rules.getCities().get(city).getAbbreviation(), rules.getCities().get(city)));
            } else {
                return rules.withCities(null);
            }
        }
        return rules;
    }
}
