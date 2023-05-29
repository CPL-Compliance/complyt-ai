package com.complyt.domain.sales_tax;

import com.complyt.domain.Address;
import lombok.With;

@With
public record ComplytSalesTaxRates(Address address, SalesTaxRates salesTaxRates) {
}

