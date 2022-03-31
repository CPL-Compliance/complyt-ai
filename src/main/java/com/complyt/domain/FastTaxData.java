package com.complyt.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class FastTaxData implements SalesTaxData {
    private String city;
    private String county;
    private String countyFIPS;
    private String stateName;
    private String stateAbbreviation;
    private double totalTaxRate;
    private String totalTaxExempt;
    private double stateRate;
    private double cityRate;
    private double countyRate;
    private double countyDistrictRate;
    private int cityDistrictRate;
}