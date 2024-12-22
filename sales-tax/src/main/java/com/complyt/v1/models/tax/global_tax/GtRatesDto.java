package com.complyt.v1.models.tax.global_tax;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

import java.math.BigDecimal;

@With
@Schema(name = "GtRates")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GtRatesDto(
        BigDecimal countryRate,
        BigDecimal regionRate,
        BigDecimal taxRate
) {
}