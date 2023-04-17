package com.complyt.v1.model;

import lombok.With;

@With
public record AddressWithSalesTaxRatesDto(AddressDto address, SalesTaxRatesDto salesTaxRates) {
}
