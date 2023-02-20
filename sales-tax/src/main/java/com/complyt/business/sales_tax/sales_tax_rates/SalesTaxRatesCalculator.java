package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.SalesTaxRules;
import lombok.NonNull;

public interface SalesTaxRatesCalculator<T extends SalesTaxRules> {
    SalesTaxRate calculate(@NonNull T t, @NonNull SalesTaxRate originalSalesTaxRate);
}
