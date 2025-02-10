package com.complyt.domain.sales_tax;

import com.complyt.domain.transaction.MatchedAddressData;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

import java.util.UUID;

@With
@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields in JSON response
public record ComplytSalesTaxRates(UUID complytId, MatchedAddressData matchedAddressData, SalesTaxRates salesTaxRates) implements ComplytInternalRates {
}