package com.complyt.v1.model;

public record SalesTaxRatesDto(float cityDistrictRate, float cityRate, float countyDistrictRate, float countyRate,
                              float stateRate, float taxRate) {
}
