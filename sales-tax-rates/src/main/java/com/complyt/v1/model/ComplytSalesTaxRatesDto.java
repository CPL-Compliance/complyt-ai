package com.complyt.v1.model;

import lombok.With;

@With
public record ComplytSalesTaxRatesDto(AddressDto address, SalesTaxRatesDto salesTaxRates) {
}
