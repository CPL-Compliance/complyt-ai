package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SalesTaxRates")
public record SalesTaxRatesDto(float cityDistrictRate, float cityRate, float countyDistrictRate, float countyRate,
                              float stateRate, float taxRate) {
}
