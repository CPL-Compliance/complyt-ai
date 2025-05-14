package com.complyt.domain.internal_rates;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class FilingMetaData {
    String city;
    String county;

    BigDecimal other1Rate;
    BigDecimal other2Rate;
    BigDecimal other3Rate;
    BigDecimal other4Rate;

    String countyRptCode;
    String cityRptCode;
    String mtaName;
    String mtaNumber;
    String spdName;
    String spdNumber;
    String other1Name;
    String other1Number;
    String other2Name;
    String other2Number;
    String other3Name;
    String other3Number;
    String other4Name;
    String other4Number;

    String fipsCounty;
}
