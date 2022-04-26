package com.complyt.domain.sales_tax;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SalesTaxRate {
    private float cityDistrictRate;
    private float cityRate;
    private float countyDistrictRate;
    private float countyRate;
    private float stateRate;
    private float taxRate;
}
