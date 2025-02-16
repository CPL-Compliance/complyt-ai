package com.complyt.domain.common_rates;


import com.complyt.domain.enums.SalesTaxSources;
import com.complyt.domain.internal_rates.InternalSalesTaxRatesMetaData;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

import java.util.UUID;

/**
 * the minimum common denominator between internal and external rates
 * @param address - State, County, City
 * @param salesTaxRates - ComplytId, state rate, county rate, city rate, tax rate and source
 */
@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommonSalesTaxRates(UUID complytId, CommonAddress address, CommonRates salesTaxRates, SalesTaxSources source, InternalSalesTaxRatesMetaData ratesMetaData) {
}
