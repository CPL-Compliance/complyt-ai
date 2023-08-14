package com.complyt.domain.sales_tax;

import lombok.With;

@With
public record SalesTaxRates(double cityRate, double countyRate, double stateRate, double taxRate,
                            double combinedDistrictRate, RatesMetaData ratesMetaData) {

    public static SalesTaxRates zeroSalesTaxRate() {
        return new SalesTaxRates(0, 0, 0, 0, 0, new RatesMetaData(0, 0));
    }
}
