package com.complyt.domain.fast_tax;

import com.complyt.domain.SalesTaxData;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

@Getter
@EqualsAndHashCode
@With
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class FastTaxGetByCityCountyData implements SalesTaxData {

    String city;
    String county;
    String countyFips;
    String stateName;
    String stateAbbreviation;
    String totalTaxRate;
    String totalTaxExempt;
    String stateRate;
    String cityRate;
    String countyRate;
    String countyDistrictRate;
    String cityDistrictRate;

    @Override
    public boolean isUnincorporated() {
        return false;
    }
}
