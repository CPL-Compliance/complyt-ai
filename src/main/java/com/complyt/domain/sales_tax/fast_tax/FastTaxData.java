package com.complyt.domain.sales_tax.fast_tax;

import com.complyt.domain.sales_tax.SalesTaxData;
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
public class FastTaxData extends SalesTaxData {
    private String matchLevel;
    private List<TaxInfoItem> taxInfoItems;
}

