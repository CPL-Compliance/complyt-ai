package com.complyt.domain.sales_tax;

import lombok.*;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class SalesTaxRate {
    private final float cityDistrictRate;
    private final float cityRate;
    private final float countyDistrictRate;
    private final float countyRate;
    private final float stateRate;
    private final float taxRate;

    public static SalesTaxRate zeroSalesTaxRate(){
        return new SalesTaxRate(0, 0, 0, 0, 0, 0);
    }
}
