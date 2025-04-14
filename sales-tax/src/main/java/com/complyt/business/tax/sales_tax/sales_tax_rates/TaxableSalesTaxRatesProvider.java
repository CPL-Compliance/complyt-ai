package com.complyt.business.tax.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.transaction.ShippingAddress;

public interface TaxableSalesTaxRatesProvider<T> {
    T setSalesTaxRates(T t, SalesTaxRates salesTaxRates, ShippingAddress address);
}
