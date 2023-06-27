package com.complyt.domain;

import lombok.With;

@With
public record SalesTaxRates(float cityRate, float countyRate, float stateRate, float taxRate, float combinedDistrictRate, RatesMetaData ratesMetaData) {
    public static SalesTaxRates zeroSalesTaxRates() {
        return new SalesTaxRates(0, 0, 0, 0, 0, null);
    }
}
