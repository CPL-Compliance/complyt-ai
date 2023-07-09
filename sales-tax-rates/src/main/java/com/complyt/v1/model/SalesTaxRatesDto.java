package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SalesTaxRates")
public record SalesTaxRatesDto(float cityRate,float countyRate, float stateRate, float taxRate, float combinedDistrictRate,
                               RatesMetaDataDto ratesMetaData) {
}
