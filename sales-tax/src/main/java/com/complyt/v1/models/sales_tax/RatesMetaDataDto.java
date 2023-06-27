package com.complyt.v1.models.sales_tax;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

@With
@Schema(name = "RatesMetaData")
public record RatesMetaDataDto(float cityDistrictRate, float countyDistrictRate) {
}
