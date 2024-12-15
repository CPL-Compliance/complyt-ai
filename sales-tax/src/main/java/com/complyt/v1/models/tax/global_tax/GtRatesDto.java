package com.complyt.v1.models.tax.global_tax;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

import java.math.BigDecimal;

@With
@Schema(name = "GtRates")
public record GtRatesDto(
        BigDecimal countryRate,
        BigDecimal regionRate,
        BigDecimal taxRate
) {
}