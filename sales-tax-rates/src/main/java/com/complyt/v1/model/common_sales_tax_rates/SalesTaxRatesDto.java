package com.complyt.v1.model.common_sales_tax_rates;

import com.complyt.domain.RatesMetaData;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(name = "SalesTaxRates")
@With
@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields in JSON response
public record SalesTaxRatesDto(BigDecimal stateRate, BigDecimal countyRate, BigDecimal cityRate,
                               BigDecimal combinedDistrictRate, RatesMetaData ratesMetaData,
                               BigDecimal mtaRate, BigDecimal spdRate, BigDecimal otherRate,
                               BigDecimal taxRate) {
}
