package com.complyt.domain.sales_tax.product_classification;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class Rates {
    private final float cityDistrictRate;
    private final float cityRate;
    private final float countyDistrictRate;
    private final float countyRate;
    private final float stateRate;
    private final float taxRate;
}
