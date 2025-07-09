package io.complyt.domain.transaction.tax;

import io.complyt.domain.sales_tax.ComplytInternalRates;
import lombok.With;

@With
public record ComplytGtRates(GtAddress gtAddress, GtRates gtRates) implements ComplytInternalRates {
}
