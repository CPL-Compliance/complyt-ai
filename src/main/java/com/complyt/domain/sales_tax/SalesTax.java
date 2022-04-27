package com.complyt.domain.sales_tax;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
public class SalesTax {
    private final SalesTaxRate salesTaxRate;
    private float amount;

    @Override
    public String toString(){
        return "taxRate : " + salesTaxRate.getTaxRate() + " cityRate : " + salesTaxRate.getCityRate() + " getCityDistrictRate : " + salesTaxRate.getCityDistrictRate() +
                " stateRate : " + salesTaxRate.getStateRate() + " countyRate : " + salesTaxRate.getCountyRate() + " stateRate : " + salesTaxRate.getStateRate() +
                " amount : " + amount;
    }
}
