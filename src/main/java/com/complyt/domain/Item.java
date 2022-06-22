package com.complyt.domain;

import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@With
@AllArgsConstructor
public class Item {
    private float unitPrice;
    private int quantity;
    private float totalPrice;
    private String description;
    private String name;
    private String taxCode;
    private JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    private SalesTaxRate salesTaxRate;
    private boolean manualSalesTax;
    private float manualSalesTaxRate;

    public float getManualSalesTaxAmount(){
        return manualSalesTaxRate * totalPrice;
    }
}