package com.complyt.domain.fast_tax;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
@FieldDefaults(level = AccessLevel.PRIVATE)
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