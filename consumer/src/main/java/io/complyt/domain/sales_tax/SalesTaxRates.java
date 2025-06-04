package io.complyt.domain.sales_tax;

import io.complyt.domain.TaxRates;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

import java.math.BigDecimal;

@With
@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields in JSON response
public record SalesTaxRates(BigDecimal stateRate, BigDecimal countyRate, BigDecimal cityRate,
        BigDecimal combinedDistrictRate, RatesMetaData ratesMetaData,
        BigDecimal mtaRate, BigDecimal spdRate, BigDecimal otherRate,
        BigDecimal taxRate)

        implements TaxRates {


    public static SalesTaxRates zeroSalesTaxRate() {
        return new SalesTaxRates(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                new RatesMetaData(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getTaxRate() {
        return this.taxRate;
    }
}
