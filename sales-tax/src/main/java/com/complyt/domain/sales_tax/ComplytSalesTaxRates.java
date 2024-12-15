package com.complyt.domain.sales_tax;

import com.complyt.domain.sales_tax.complyt_sales_tax_rates.CommonAddress;
import com.complyt.domain.transaction.Address;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

import java.util.UUID;

@With
@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields in JSON response
public record ComplytSalesTaxRates(UUID complytId, Address address, SalesTaxRates salesTaxRates) implements ComplytInternalRates {
}