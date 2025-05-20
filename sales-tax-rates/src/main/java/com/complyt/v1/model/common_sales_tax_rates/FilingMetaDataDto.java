package com.complyt.v1.model.common_sales_tax_rates;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FilingMetaDataDto(String city, String county, BigDecimal other1Rate, BigDecimal other2Rate,
                                BigDecimal other3Rate, BigDecimal other4Rate, String countyRptCode, String cityRptCode,
                                String mtaName, String mtaNumber, String spdName, String spdNumber, String other1Name,
                                String other1Number, String other2Name, String other2Number, String other3Name,
                                String other3Number, String other4Name, String other4Number, String fipsCounty) {
}
