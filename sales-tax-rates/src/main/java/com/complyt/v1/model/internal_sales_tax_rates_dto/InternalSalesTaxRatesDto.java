package com.complyt.v1.model.internal_sales_tax_rates_dto;

import jakarta.validation.Valid;
import lombok.With;

@With
public record InternalSalesTaxRatesDto(
        @Valid InternalAddressDto address,
        @Valid InternalRateDto rates
) {
}