package com.complyt.v1.model;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@Schema(name = "SalesTaxRate")
public class SalesTaxRateDto {
    private final float cityDistrictRate;
    private final float cityRate;
    private final float countyDistrictRate;
    private final float countyRate;
    private final float stateRate;
    private final float taxRate;
}
