package com.complyt.domain.sales_tax.complyt_sales_tax_rates;

import com.complyt.domain.sales_tax.RatesMetaData;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

import java.math.BigDecimal;

@With
@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields in JSON response
public record CommonRates (BigDecimal stateRate, BigDecimal countyRate, BigDecimal cityRate,
                           BigDecimal combinedDistrictRate, RatesMetaData ratesMetaData,
                           BigDecimal mtaRate, BigDecimal spdRate, BigDecimal otherRate,
                           BigDecimal taxRate) {
}