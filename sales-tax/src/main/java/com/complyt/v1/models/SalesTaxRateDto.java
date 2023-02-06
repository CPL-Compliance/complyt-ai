package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

@With
@Schema(name = "SalesTaxRate")
public record SalesTaxRateDto(float cityDistrictRate, float cityRate, float countyDistrictRate, float countyRate,
                              float stateRate, float taxRate) {
}
