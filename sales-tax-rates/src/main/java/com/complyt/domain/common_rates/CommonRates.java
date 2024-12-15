package com.complyt.domain.common_rates;

import com.complyt.domain.RatesMetaData;
import com.complyt.domain.enums.SalesTaxSources;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

import java.math.BigDecimal;
import java.util.UUID;

@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommonRates (BigDecimal stateRate, BigDecimal countyRate, BigDecimal cityRate,
                           BigDecimal combinedDistrictRate, RatesMetaData ratesMetaData,
                           BigDecimal mtaRate, BigDecimal spdRate, BigDecimal otherRate,
                           BigDecimal taxRate) {
}