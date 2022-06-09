package com.complyt.domain.sales_tax;

import com.complyt.v1.model.SalesTaxRateDto;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
public class SalesTax {
    private float amount;
    private SalesTaxRate salesTaxRate;
}
