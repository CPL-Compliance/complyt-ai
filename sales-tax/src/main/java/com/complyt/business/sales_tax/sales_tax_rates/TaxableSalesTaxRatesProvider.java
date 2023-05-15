package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.SalesTaxRates;

public interface TaxableSalesTaxRatesProvider<T> {
    T setSalesTaxRates(T t, SalesTaxRates salesTaxRates, Address address);
}
