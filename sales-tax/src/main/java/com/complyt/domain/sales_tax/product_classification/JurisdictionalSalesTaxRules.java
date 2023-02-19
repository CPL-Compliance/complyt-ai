package com.complyt.domain.sales_tax.product_classification;

import lombok.*;

import java.util.Map;

@With
public record JurisdictionalSalesTaxRules(String name, String abbreviation, boolean taxable, boolean specialTreatment,
                                          CalculationType calculationType, String description, float calculationValue,
                                          Map<String, CitySalesTaxRules> cities) implements SalesTaxRules {
    public boolean calculatedByPercentageCheck() {
        return taxable && specialTreatment && calculationType == CalculationType.PERCENTAGE;
    }

}
