package com.complyt.domain.sales_tax.product_classification;

import java.math.BigDecimal;

public interface TaxRules {

    boolean isTaxable();

    boolean isSpecialTreatment();

    CalculationType getCalculationType();

    BigDecimal getCalculationValue();

}
