package io.complyt.domain.sales_tax.product_classification;

import io.complyt.domain.sales_tax.product_classification.CalculationType;

import java.math.BigDecimal;

public interface TaxRules {

    boolean isTaxable();

    boolean isSpecialTreatment();

    CalculationType getCalculationType();

    BigDecimal getCalculationValue();

}
