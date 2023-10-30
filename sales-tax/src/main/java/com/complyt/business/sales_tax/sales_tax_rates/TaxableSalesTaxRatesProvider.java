package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.transaction.Address;

public interface TaxableSalesTaxRatesProvider<T> {
    T setSalesTaxRates(T t, SalesTaxRates salesTaxRates, Address address);
}
