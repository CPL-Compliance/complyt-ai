package com.complyt.domain.sales_tax.product_classification;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@With
@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public final class JurisdictionalSalesTaxRules implements SalesTaxRules {
    private final String name;
    private final String abbreviation;
    private final boolean taxable;
    private final boolean specialTreatment;
    private final CalculationType calculationType;
    private final String description;
    private final BigDecimal calculationValue;
    private final Map<String, CitySalesTaxRules> cities;

    public boolean calculatedByPercentageCheck() {
        return taxable && specialTreatment && calculationType == CalculationType.PERCENTAGE;
    }

}
