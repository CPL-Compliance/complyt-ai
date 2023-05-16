package com.complyt.domain.zip_tax;

import com.complyt.domain.SalesTaxData;
import lombok.Value;
import lombok.With;

import java.util.List;

@With
@Value
public class ZipTaxData implements SalesTaxData {
    String version;
    long rCode;
    List<Result> results;

    @Override
    public boolean isUnincorporated() {
        return false;
    }

}