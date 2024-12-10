package com.complyt.v1.models.tax.sales_tax;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "RatesMetaData")
public record RatesMetaDataDto(BigDecimal cityDistrictRate, BigDecimal countyDistrictRate, BigDecimal specialDistrictRate) {
}
