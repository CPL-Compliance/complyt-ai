package com.complyt.domain;

import com.complyt.domain.sales_tax.SalesTaxRate;
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
    private SalesTaxRate salesTaxRate;
}
