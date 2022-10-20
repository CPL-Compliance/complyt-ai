package com.complyt.domain.sales_tax.product_classification;

import lombok.*;

import java.util.Map;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class JurisdictionalSalesTaxRules {
    private final String name;
    private final String abbreviation;
    private final boolean taxable;
    private final boolean specialTreatment;
    private final CalculationType calculationType;
    private final String description;
    private final float calculationValue;
    private final Map<String, CitySalesTaxRules> cities;

    public boolean calculatedByPercentageCheck() {
        return taxable && specialTreatment && calculationType == CalculationType.PERCENTAGE;
    }
}
