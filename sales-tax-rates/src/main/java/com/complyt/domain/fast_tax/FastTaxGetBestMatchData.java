package com.complyt.domain.fast_tax;

import com.complyt.domain.SalesTaxData;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.List;

@Getter
@EqualsAndHashCode
@With
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class FastTaxGetBestMatchData implements SalesTaxData {
    String matchLevel;
    List<TaxInfoItem> taxInfoItems;
    String UNINCORPORATED_CODE = "1";

    @Override
    public boolean isUnincorporated() {
        return taxInfoItems != null && taxInfoItems.get(0).notesCodes().equals(UNINCORPORATED_CODE);
    }

}