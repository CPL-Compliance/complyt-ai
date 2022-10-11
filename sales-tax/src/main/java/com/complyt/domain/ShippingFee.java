package com.complyt.domain;

import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@With
@AllArgsConstructor
public class ShippingFee {
    private final boolean manualSalesTax;
    private final float manualSalesTaxRate;
    private final float price;
    private final JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    private final SalesTaxRate salesTaxRate;

    public float getManualSalesTaxAmount(){
        return manualSalesTaxRate * price;
    }
}
