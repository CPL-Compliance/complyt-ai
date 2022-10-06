package com.complyt.domain.sales_tax.zip_tax;

import com.complyt.domain.sales_tax.SalesTaxData;
import lombok.*;

import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@With
@NoArgsConstructor
@AllArgsConstructor
public class ZipTaxData implements SalesTaxData {
    private String version;
    private long rCode;
    private List<Result> results;

    @Override
    public boolean isUnincorporated() {
        return false;
    }
}
