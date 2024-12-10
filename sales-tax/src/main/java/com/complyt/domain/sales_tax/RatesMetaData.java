package com.complyt.domain.sales_tax;

import lombok.With;

import java.math.BigDecimal;

@With
public record RatesMetaData(BigDecimal cityDistrictRate, BigDecimal countyDistrictRate, BigDecimal specialDistrictRate) {
}
