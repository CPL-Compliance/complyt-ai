package com.complyt.v1.models;

import lombok.With;

@With
public record ComplytSalesTaxRatesDto(MandatoryAddressDto address, SalesTaxRatesDto salesTaxRates) {
}