package com.complyt.domain.sales_tax;

import lombok.With;

@With
public record SalesTaxRates(float cityDistrictRate, float cityRate, float countyDistrictRate, float countyRate,
                            float stateRate, float taxRate) {
    public static SalesTaxRates zeroSalesTaxRate() {
        return new SalesTaxRates(0, 0, 0, 0, 0, 0);
    }
}
