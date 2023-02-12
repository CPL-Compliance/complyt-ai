package com.complyt.domain.sales_tax.product_classification;

public record CitySalesTaxRules(String name, String abbreviation, boolean taxable, boolean specialTreatment,
                                CalculationType calculationType, String description,
                                float calculationValue) implements SalesTaxRules {
}
