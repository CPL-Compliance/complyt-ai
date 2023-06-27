package com.complyt.v1.models;

import com.complyt.v1.models.sales_tax.SalesTaxRatesDto;
import lombok.With;

@With
public record ComplytSalesTaxRatesDto(MandatoryAddressDto address, SalesTaxRatesDto salesTaxRates) {
}