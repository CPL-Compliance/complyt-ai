package io.complyt.domain.transaction.tax;

import io.complyt.domain.TaxRates;
import lombok.With;

import java.math.BigDecimal;

@With
public record GtRates(BigDecimal countryRate, BigDecimal regionRate, BigDecimal taxRate) implements TaxRates {

    public static GtRates zeroGtRates() {
        return new GtRates(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getTaxRate() {
        return taxRate;
    }
}
