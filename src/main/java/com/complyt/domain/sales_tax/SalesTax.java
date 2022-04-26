package com.complyt.domain.sales_tax;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
public class SalesTax {
    private SalesTaxRate salesTaxRate;
    private float amount;
}
