package com.complyt.domain.sales_tax;

import com.complyt.domain.transaction.tax.GtRates;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

import java.math.BigDecimal;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@With
public record SalesTax(
        UUID complytId,
        BigDecimal amount,
        BigDecimal rate,
        SalesTaxRates salesTaxRates,
        GtRates gtRates,
        FilingMetaData filingMetaData) {
}