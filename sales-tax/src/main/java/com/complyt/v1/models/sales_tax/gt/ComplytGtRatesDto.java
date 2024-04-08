package com.complyt.v1.models.sales_tax.gt;

import lombok.With;

@With
public record ComplytGtRatesDto(GtAddressDto gtAddress, GtRatesDto gtRates) {
}
