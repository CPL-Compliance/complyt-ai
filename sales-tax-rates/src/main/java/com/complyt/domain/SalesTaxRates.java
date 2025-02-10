package com.complyt.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

import java.math.BigDecimal;

@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SalesTaxRates(BigDecimal cityRate, BigDecimal countyRate, BigDecimal stateRate, BigDecimal taxRate,
                            BigDecimal combinedDistrictRate, RatesMetaData ratesMetaData) {
}
