package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.SalesTaxRules;
import lombok.NonNull;

public interface SalesTaxRatesCalculator<T extends SalesTaxRules> {
    SalesTaxRates calculate(@NonNull T t, @NonNull SalesTaxRates originalSalesTaxRate);
}
