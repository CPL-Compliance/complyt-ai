package com.complyt.domain.sales_tax.product_classification;

import lombok.*;

import java.math.BigDecimal;

@With
@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public final class CitySalesTaxRules implements SalesTaxRules {
    private final String name;
    private final String abbreviation;
    private final boolean taxable;
    private final boolean specialTreatment;
    private final CalculationType calculationType;
    private final String description;
    private final BigDecimal calculationValue;

}
