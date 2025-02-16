package com.complyt.v1.model.internal_sales_tax_rates_dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields in JSON response
public class InternalSalesTaxRatesMetaDataDto {
    String recordType;
    String stateAbbrev;
    String stateUseTax;
    String countyUseTax;
    String cityUseTax;
    String mtaUseTax;
    String spdUseTax;
    String other1UseTax;
    String other2UseTax;
    String other3UseTax;
    String other4UseTax;
    String totalUseTax;
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
    String taxShippingAlone;
    String taxShippingAndHandlingTogether;
    String fipsState;
    String fipsCounty;
    String fipsCity;
    String fipsGeocode;
    String countyTaxCollectedBy;
    String cityTaxCollectedBy;
    String countyTaxableMax;
    String countyTaxOverMax;
    String cityTaxableMax;
    String cityTaxOverMax;
}
