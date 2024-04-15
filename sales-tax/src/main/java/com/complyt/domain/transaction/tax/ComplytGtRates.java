package com.complyt.domain.transaction.tax;

import com.complyt.domain.sales_tax.ComplytInternalRates;
import lombok.With;

@With
public record ComplytGtRates(GtAddress gtAddress, GtRates gtRates) implements ComplytInternalRates {
}
