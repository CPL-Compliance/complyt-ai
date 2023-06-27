package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RatesMetaData")
public record RatesMetaDataDto(float cityDistrictRate, float countyDistrictRate) {
}
