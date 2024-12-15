package com.complyt.domain.internal_rates;

import lombok.Value;

@Value
public class InternalSalesTaxRatesMetaData {
    String mtaName;
    String spdName;
    String other1Name;
    String other2Name;
    String other3Name;
    String other4Name;
    String mtaNumber;
    String spdNumber;
    String other1Number;
    String other2Number;
    String other3Number;
    String other4Number;
    String fipsState;
    String fipsCounty;
    String fipsCity;
    String fipsGeocode;
    String countyTaxableMax;
    String countyTaxOverMax;
    String cityTaxableMax;
    String cityTaxOverMax;
    String taxShippingAlone;
    String taxShippingAndHandlingTogether;
}
