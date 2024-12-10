package com.complyt.v1.models.tax.global_tax;

import lombok.With;

@With
public record ComplytGtRatesDto(GtAddressDto gtAddress, GtRatesDto gtRates) {
}
