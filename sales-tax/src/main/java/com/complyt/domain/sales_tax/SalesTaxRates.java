package com.complyt.domain.sales_tax;

import com.complyt.domain.TaxRates;
import lombok.With;

import java.math.BigDecimal;

@With
public record SalesTaxRates(BigDecimal cityRate, BigDecimal countyRate, BigDecimal stateRate, BigDecimal taxRate,
                            BigDecimal combinedDistrictRate, RatesMetaData ratesMetaData) implements TaxRates {



    public static SalesTaxRates zeroSalesTaxRate() {
        return new SalesTaxRates(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                new RatesMetaData(BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Override
    public BigDecimal getTaxRate() {
        return this.taxRate;
    }
}
