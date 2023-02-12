package com.complyt.domain.sales_tax.product_classification;

public interface SalesTaxRules {

    boolean taxable();

    boolean specialTreatment();

    CalculationType calculationType();

    float calculationValue();

    default boolean calculatedByPercentageCheck() {
        return taxable() && specialTreatment() && calculationType() == CalculationType.PERCENTAGE;
    }
}
