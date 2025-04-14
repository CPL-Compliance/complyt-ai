package com.complyt.business.tax.sales_tax.models;

import com.complyt.domain.sales_tax.RatesMetaData;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "SalesTaxRates")
public record InternalSalesTaxRatesDto(BigDecimal stateRate, BigDecimal countyRate, BigDecimal cityRate,
                                       BigDecimal combinedDistrictRate, RatesMetaData ratesMetaData,
                                       BigDecimal mtaRate, BigDecimal spdRate, BigDecimal otherRate,
                                       BigDecimal taxRate) {
}
