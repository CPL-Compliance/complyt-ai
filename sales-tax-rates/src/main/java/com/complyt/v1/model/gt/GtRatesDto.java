package com.complyt.v1.model.gt;

import lombok.With;

import java.math.BigDecimal;

@With
public record GtRatesDto(
        BigDecimal countryRate,
        BigDecimal regionRate,
        BigDecimal taxRate
) {
}
