package com.complyt.domain.fast_tax;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.With;

import java.util.List;

@With
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public record TaxInfoItem(String city, String cityDistrictRate, String cityRate, String county,
                          String countyDistrictRate, String countyRate,
                          List<InformationComponent> informationComponents, String notesCodes, String notesDesc,
                          String specialDistrictRate, String stateAbbreviation, String stateName, String stateRate,
                          String taxRate, String totalTaxExempt, String zip) {
}