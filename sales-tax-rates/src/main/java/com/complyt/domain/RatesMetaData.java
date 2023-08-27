package com.complyt.domain;

import lombok.With;

import java.math.BigDecimal;

@With
public record RatesMetaData(BigDecimal cityDistrictRate, BigDecimal countyDistrictRate) {
}
