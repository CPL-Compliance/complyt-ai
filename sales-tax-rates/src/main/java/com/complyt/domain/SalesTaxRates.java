package com.complyt.domain;

import lombok.With;

import java.math.BigDecimal;

@With
public record SalesTaxRates(BigDecimal cityRate, BigDecimal countyRate, BigDecimal stateRate, BigDecimal taxRate,
                            BigDecimal combinedDistrictRate, RatesMetaData ratesMetaData) {
}
