package io.complyt.domain.sales_tax;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.complyt.domain.transaction.tax.GtRates;
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