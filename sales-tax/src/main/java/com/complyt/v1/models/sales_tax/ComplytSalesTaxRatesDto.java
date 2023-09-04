package com.complyt.v1.models.sales_tax;

import com.complyt.v1.models.transaction.MandatoryAddressDto;
import lombok.With;

@With
public record ComplytSalesTaxRatesDto(MandatoryAddressDto address, SalesTaxRatesDto salesTaxRates) {
}