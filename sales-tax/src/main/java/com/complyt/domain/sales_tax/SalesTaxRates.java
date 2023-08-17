package com.complyt.domain.sales_tax;

import lombok.With;

import java.math.BigDecimal;

@With
public record SalesTaxRates(BigDecimal cityRate, BigDecimal countyRate, BigDecimal stateRate, BigDecimal taxRate,
                            BigDecimal combinedDistrictRate, RatesMetaData ratesMetaData) {

    public static SalesTaxRates zeroSalesTaxRate() {
        return new SalesTaxRates(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                new RatesMetaData(BigDecimal.ZERO, BigDecimal.ZERO));
    }
}
