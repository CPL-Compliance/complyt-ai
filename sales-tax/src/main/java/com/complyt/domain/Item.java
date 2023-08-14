package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@With
@AllArgsConstructor
public class Item implements Taxable {
    private double unitPrice;
    private double quantity;
    private double totalPrice;
    private String description;
    private String name;
    private String taxCode;
    private JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    private SalesTaxRates salesTaxRates;
    private boolean manualSalesTax;
    private double manualSalesTaxRate;
    private TangibleCategory tangibleCategory;
    private TaxableCategory taxableCategory;

}