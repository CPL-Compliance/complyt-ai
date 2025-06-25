package io.complyt.domain.sales_tax.product_classification;

import java.math.BigDecimal;

public interface TaxRules {

    boolean taxable();

    boolean specialTreatment();

    CalculationType calculationType();

    BigDecimal calculationValue();

}
