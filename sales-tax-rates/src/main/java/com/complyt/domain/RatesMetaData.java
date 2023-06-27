package com.complyt.domain;

import lombok.With;

@With
public record RatesMetaData(float cityDistrictRate, float countyDistrictRate) {
}
