package com.complyt.v1.mappers;

import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import com.complyt.v1.models.JurisdictionalSalesTaxRulesDto;

public interface JurisdictionalSalesTaxRuleMapper {
    default JurisdictionalSalesTaxRulesDto combineJurisdictionalRules(JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules, JurisdictionalTaxRules jurisdictionalTaxRules) {

        return jurisdictionalSalesTaxRules != null || jurisdictionalTaxRules != null ? // the "GET" projection remove both - so we need to check if either exist first
                jurisdictionalSalesTaxRules != null ?
                new JurisdictionalSalesTaxRulesDto(jurisdictionalSalesTaxRules.getName(), jurisdictionalSalesTaxRules.getAbbreviation(),
                        jurisdictionalSalesTaxRules.isTaxable(), jurisdictionalSalesTaxRules.isSpecialTreatment(), jurisdictionalSalesTaxRules.getCalculationType(),
                        jurisdictionalSalesTaxRules.getDescription(), jurisdictionalSalesTaxRules.getCalculationValue(), jurisdictionalSalesTaxRules.getCities(), null) :
                new JurisdictionalSalesTaxRulesDto(jurisdictionalTaxRules.getName(), jurisdictionalTaxRules.getAbbreviation(),
                        jurisdictionalTaxRules.isTaxable(), jurisdictionalTaxRules.isSpecialTreatment(), jurisdictionalTaxRules.getCalculationType(),
                        jurisdictionalTaxRules.getDescription(), jurisdictionalTaxRules.getCalculationValue(), null, jurisdictionalTaxRules.getRegions())
                // both are null - we are in a "projection"
                : null;
    }
}
