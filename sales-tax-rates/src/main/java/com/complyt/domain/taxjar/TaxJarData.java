package com.complyt.domain.taxjar;

import com.complyt.domain.SalesTaxData;
import com.taxjar.model.rates.Rate;
import com.taxjar.model.rates.RateResponse;
import lombok.*;

@With
@Getter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class TaxJarData extends RateResponse implements SalesTaxData {

    Rate rate;

    @Override
    public boolean isUnincorporated() {
        return false;
    }
}
