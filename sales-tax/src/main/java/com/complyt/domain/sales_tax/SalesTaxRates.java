package com.complyt.domain.sales_tax;

import lombok.With;

@With
public record SalesTaxRates(float cityRate, float countyRate, float stateRate, float taxRate,
                            float combinedDistrictRate, RatesMetaData ratesMetaData) {

    public static SalesTaxRates zeroSalesTaxRate() {
        return new SalesTaxRates(0, 0, 0, 0, 0, new RatesMetaData(0, 0));
    }
}
