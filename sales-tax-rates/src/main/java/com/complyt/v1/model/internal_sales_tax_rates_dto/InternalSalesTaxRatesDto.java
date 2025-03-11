package com.complyt.v1.model.internal_sales_tax_rates_dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InternalSalesTaxRatesDto(
        UUID complytId,
        @Valid InternalAddressDto address,
        @Valid InternalRatesDto salesTaxRates,
        @Valid InternalEffectiveDatesDto effectiveDates,
        @Valid InternalSalesTaxRatesMetaDataDto internalSalesTaxRatesMetaData,
        LocalDateTime createdDate,
        LocalDateTime expiredDate,
        LocalDateTime appliedDate,
        UUID updatedFrom,
        UUID updatedTo
) {
}