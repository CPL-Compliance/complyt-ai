package com.complyt.domain.sales_tax.product_classification;

public interface SalesTaxRules {

    boolean isTaxable();

    boolean isSpecialTreatment();

    CalculationType getCalculationType();

    float getCalculationValue();

}
