package com.complyt.domain.sales_tax.product_classification;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@With
@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class JurisdictionalTaxRules implements TaxRules {
    String name;
    String abbreviation;
    boolean taxable;
    boolean specialTreatment;
    CalculationType calculationType;
    String description;
    BigDecimal calculationValue;

    Map<String, SubJurisdictionalTaxRules> regions;

    public boolean calculatedByPercentageCheck() {
        return taxable && specialTreatment && calculationType == CalculationType.PERCENTAGE;
    }

    @Override
    public BigDecimal getCalculationValue() {
        return calculationValue != null ? calculationValue : BigDecimal.ZERO;
    }

}
