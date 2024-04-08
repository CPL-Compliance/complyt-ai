package com.complyt.domain.sales_tax;

import com.complyt.domain.transaction.tax.GtRates;
import lombok.With;

import java.math.BigDecimal;

@With
public record SalesTax(BigDecimal amount, BigDecimal rate, SalesTaxRates salesTaxRates, GtRates gtRates) {
}