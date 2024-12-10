package com.complyt.business.tax.sales_tax.models;

import com.complyt.v1.models.tax.sales_tax.SalesTaxRatesAddressDto;
import lombok.With;

import java.util.UUID;

@With
public record ComplytInternalSalesTaxRatesDto(UUID complytId, SalesTaxRatesAddressDto address, InternalSalesTaxRatesDto salesTaxRates, String source) {
}