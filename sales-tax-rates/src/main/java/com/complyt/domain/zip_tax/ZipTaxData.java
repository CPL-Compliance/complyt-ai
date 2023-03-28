package com.complyt.domain.zip_tax;

import com.complyt.domain.SalesTaxData;
import lombok.*;

import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@With
@AllArgsConstructor
@NoArgsConstructor
public class ZipTaxData implements SalesTaxData {
    private String version;
    private long rCode;
    private List<Result> results;

    @Override
    public boolean isUnincorporated() {
        return false;
    }

}