package com.complyt.domain.sales_tax;

import com.complyt.domain.transaction.tax.GtRates;
import lombok.With;

import java.math.BigDecimal;
import java.util.UUID;

@With
public record SalesTax(
        UUID complytId,
        BigDecimal amount,
        BigDecimal rate,
        SalesTaxRates salesTaxRates,
        GtRates gtRates) {
}