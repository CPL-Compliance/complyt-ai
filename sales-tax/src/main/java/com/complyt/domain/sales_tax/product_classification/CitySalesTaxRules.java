package com.complyt.domain.sales_tax.product_classification;

import lombok.With;

@With
public record CitySalesTaxRules(String name, String abbreviation, boolean taxable, boolean specialTreatment,
                                CalculationType calculationType, String description,
                                float calculationValue) implements SalesTaxRules {
}
