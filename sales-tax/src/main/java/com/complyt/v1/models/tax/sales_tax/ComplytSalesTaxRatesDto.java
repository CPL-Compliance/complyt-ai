package com.complyt.v1.models.tax.sales_tax;

import lombok.With;

@With
public record ComplytSalesTaxRatesDto(SalesTaxRatesAddressDto address, SalesTaxRatesDto salesTaxRates) {
}