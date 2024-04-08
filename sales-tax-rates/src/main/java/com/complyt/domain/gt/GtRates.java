package com.complyt.domain.gt;

import lombok.With;

import java.math.BigDecimal;

@With
public record GtRates(
        BigDecimal countryRate,
        BigDecimal regionRate,
        BigDecimal taxRate
) {
}