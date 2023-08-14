package com.complyt.domain;

import lombok.With;

@With
public record RatesMetaData(double cityDistrictRate, double countyDistrictRate) {
}
