package com.complyt.v1.models.tax.sales_tax;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields in JSON response
@Schema(name = "RatesMetaData")
public record RatesMetaDataDto(BigDecimal cityDistrictRate, BigDecimal countyDistrictRate, BigDecimal specialDistrictRate) {
}
