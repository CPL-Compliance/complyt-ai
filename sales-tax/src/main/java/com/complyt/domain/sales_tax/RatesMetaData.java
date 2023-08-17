package com.complyt.domain.sales_tax;

import java.math.BigDecimal;

public record RatesMetaData(BigDecimal cityDistrictRate, BigDecimal countyDistrictRate) {
}
