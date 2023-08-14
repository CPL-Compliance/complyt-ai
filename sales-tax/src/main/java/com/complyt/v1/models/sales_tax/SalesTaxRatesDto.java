package com.complyt.v1.models.sales_tax;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SalesTaxRates")
public record SalesTaxRatesDto(double cityRate, double countyRate, double stateRate, double taxRate,
                               double combinedDistrictRate, RatesMetaDataDto ratesMetaData) {
}
