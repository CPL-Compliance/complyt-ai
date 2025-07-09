package io.complyt.domain.sales_tax.product_classification;

import java.math.BigDecimal;

public record SubJurisdictionalTaxRules(
        String name,
        String abbreviation,
        boolean taxable,
        boolean specialTreatment,
        CalculationType calculationType,
        String description,
        BigDecimal calculationValue
) implements TaxRules {
}
