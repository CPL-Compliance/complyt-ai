package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRate;
import lombok.NonNull;

public interface SalesTaxRatesCalculator<T> {
    SalesTaxRate calculate(@NonNull T t, @NonNull SalesTaxRate originalSalesTaxRate);

}
