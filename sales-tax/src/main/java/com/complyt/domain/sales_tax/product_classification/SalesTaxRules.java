package com.complyt.domain.sales_tax.product_classification;

import java.math.BigDecimal;

public interface SalesTaxRules {

    boolean isTaxable();

    boolean isSpecialTreatment();

    CalculationType getCalculationType();

    BigDecimal getCalculationValue();

}
