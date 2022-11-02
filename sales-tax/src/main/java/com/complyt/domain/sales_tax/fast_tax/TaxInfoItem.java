package com.complyt.domain.sales_tax.fast_tax;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@With
@NoArgsConstructor
@AllArgsConstructor
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class TaxInfoItem {
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
