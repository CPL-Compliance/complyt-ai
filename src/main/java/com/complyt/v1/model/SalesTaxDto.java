package com.complyt.v1.model;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
public class SalesTaxDto {
    private final SalesTaxRateDto salesTaxRate;
    private float amount;
}
