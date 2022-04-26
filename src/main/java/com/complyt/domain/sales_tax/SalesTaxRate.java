package com.complyt.domain.sales_tax;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SalesTaxRate {
    private String cityDistrictRate;
    private String cityRate;
    private String countyDistrictRate;
    private String countyRate;
    private String stateRate;
    private String taxRate;
}
