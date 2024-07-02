package com.complyt.business.strategy;

import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;

import java.util.Map;

public interface NonUsaAddressRegionExtractor {

    default JurisdictionalTaxRules extractRegionIfExists(JurisdictionalTaxRules rules, String region) {
        if (rules.getRegions() != null) {

            if (rules.getRegions().get(region) != null) {
                return rules.withRegions(Map.of(rules.getRegions().get(region).getAbbreviation(), rules.getRegions().get(region)));
            } else {
                return rules.withRegions(null);
            }
        }
        return rules;
    }
}
