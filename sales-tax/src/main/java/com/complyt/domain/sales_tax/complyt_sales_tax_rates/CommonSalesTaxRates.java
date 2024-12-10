package com.complyt.domain.sales_tax.complyt_sales_tax_rates;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

import java.util.UUID;

/**
 * the minimum common denominator between internal and external rates
 * @param address - State, County, City
 * @param salesTaxRates - ComplytId, state rate, county rate, city rate, tax rate and source
 */
@With
@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields in JSON response
public record CommonSalesTaxRates(UUID complytId, CommonAddress address, CommonRates salesTaxRates, String source) {
}