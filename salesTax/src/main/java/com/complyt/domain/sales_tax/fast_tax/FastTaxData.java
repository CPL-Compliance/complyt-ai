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
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class FastTaxData implements SalesTaxData {
    private String matchLevel;
    private List<TaxInfoItem> taxInfoItems;
}
