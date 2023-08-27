package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "SalesTaxRates")
public record SalesTaxRatesDto(BigDecimal cityRate, BigDecimal countyRate, BigDecimal stateRate, BigDecimal taxRate, BigDecimal combinedDistrictRate,
                               RatesMetaDataDto ratesMetaData) {
}
