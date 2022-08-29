package com.complyt.domain.sales_tax.zip_tax;

import com.complyt.domain.sales_tax.SalesTaxData;
import lombok.*;

import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@With
@AllArgsConstructor
public class ZipTaxData implements SalesTaxData {
    private String version;
    private long rCode;
    private List<Result> results;
}
