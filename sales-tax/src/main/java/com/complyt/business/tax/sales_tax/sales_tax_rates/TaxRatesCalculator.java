package com.complyt.business.tax.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.TaxRules;
import lombok.NonNull;

public interface TaxRatesCalculator<T extends TaxRules> {
    SalesTaxRates calculate(@NonNull T t, @NonNull SalesTaxRates originalSalesTaxRate);
}
