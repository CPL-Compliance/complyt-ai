package io.complyt.domain.sales_tax.product_classification;

import lombok.With;

import java.math.BigDecimal;
import java.util.Map;

@With
public record JurisdictionalSalesTaxRules(
        String name,
        String abbreviation,
        boolean taxable,
        boolean specialTreatment,
        CalculationType calculationType,
        String description,
        BigDecimal calculationValue,
        Map<String, SubJurisdictionalTaxRules> cities
) implements TaxRules {

    public boolean calculatedByPercentageCheck() {
        return taxable && specialTreatment && calculationType == CalculationType.PERCENTAGE;
    }

    @Override
    public BigDecimal calculationValue() {
        return calculationValue != null ? calculationValue : BigDecimal.ZERO;
    }
}
