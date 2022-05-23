package com.complyt.domain.sales_tax.product_classification;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Builder
public class Rates {
    private final float stateRate;
    private final float cityRate;
    private final float countyRate;
    private final float localRate;
}
