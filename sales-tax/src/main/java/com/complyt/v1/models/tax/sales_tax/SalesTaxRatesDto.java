package com.complyt.v1.models.tax.sales_tax;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields in JSON response
@Schema(name = "SalesTaxRates")
public record SalesTaxRatesDto(
        BigDecimal stateRate, BigDecimal countyRate, BigDecimal cityRate,
        BigDecimal combinedDistrictRate, RatesMetaDataDto ratesMetaData,
        BigDecimal mtaRate, BigDecimal spdRate, BigDecimal otherRate,
        BigDecimal taxRate) {
}
