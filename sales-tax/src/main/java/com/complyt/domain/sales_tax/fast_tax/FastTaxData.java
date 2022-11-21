package com.complyt.domain.sales_tax.fast_tax;

import com.complyt.domain.sales_tax.SalesTaxData;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.List;

@Getter
@EqualsAndHashCode
@With
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class FastTaxData implements SalesTaxData {
    private String matchLevel;
    private List<TaxInfoItem> taxInfoItems;
    private final String UNINCORPORATED_CODE = "1";

    @Override
    public boolean isUnincorporated() {
        return taxInfoItems != null && taxInfoItems.get(0).getNotesCodes().equals(UNINCORPORATED_CODE);
    }
}
