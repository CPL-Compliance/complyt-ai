package com.complyt.domain.fast_tax;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.List;

@Value
@Builder
@With
@AllArgsConstructor
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class TaxInfoItem {
    String city;
    String cityDistrictRate;
    String cityRate;
    String county;
    String countyDistrictRate;
    String countyRate;
    List<InformationComponent> informationComponents;
    String notesCodes;
    String notesDesc;
    String specialDistrictRate;
    String stateAbbreviation;
    String stateName;
    String stateRate;
    String taxRate;
    String totalTaxExempt;
    String zip;
}