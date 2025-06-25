package io.complyt.domain.sales_tax;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.complyt.domain.transaction.MatchedAddressData;
import lombok.With;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields in JSON response
@With
public record ComplytSalesTaxRates(UUID complytId, MatchedAddressData matchedAddressData, SalesTaxRates salesTaxRates, FilingMetaData filingMetaData) implements ComplytInternalRates {
}