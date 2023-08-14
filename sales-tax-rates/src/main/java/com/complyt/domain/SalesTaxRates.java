package com.complyt.domain;

import lombok.With;

@With
public record SalesTaxRates(double cityRate, double countyRate, double stateRate, double taxRate,
                            double combinedDistrictRate, RatesMetaData ratesMetaData) {
    public static SalesTaxRates zeroSalesTaxRates() {
        return new SalesTaxRates(0, 0, 0, 0, 0, new RatesMetaData(0, 0));
    }
}
