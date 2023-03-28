package com.complyt.domain;

import lombok.Value;
import lombok.With;

@Value
@With
public class SalesTaxRates {
    float cityDistrictRate;
    float cityRate;
    float countyDistrictRate;
    float countyRate;
    float stateRate;
    float taxRate;
}
