package com.complyt.domain.sales_tax;

import lombok.With;

@With
public record RatesMetaData(float cityDistrictRate, float countyDistrictRate) {
}
