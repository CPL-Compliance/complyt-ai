package com.complyt.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class FastTaxData implements SalesTaxData {
    private String matchLevel;
    private List<TaxInfoItem> taxInfoItems;
}

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
class TaxInfoItem {
    private String city;
    private String cityDistrictRate;
    private String cityRate;
    private String county;
    private String countyDistrictRate;
    private String countyRate;
    private List<InformationComponent> informationComponents;
    private String notesCodes;
    private String notesDesc;
    private String specialDistrictRate;
    private String stateAbbreviation;
    private String stateName;
    private String stateRate;
    private String taxRate;
    private String totalTaxExempt;
    private String zip;
}

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
class InformationComponent {
    private String name;
    private String value;
}