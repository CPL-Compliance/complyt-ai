package com.complyt.v1.models;

import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(name = "Item")
public class ItemDto {
    private float unitPrice;
    private int quantity;
    private float totalPrice;
    private String description;
    private String name;
    private String taxCode;
    private JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    private SalesTaxRateDto salesTaxRate;
    private boolean manualSalesTax;
    private float manualSalesTaxRate;
    private TangibleCategoryDto tangibleCategory;
    private TaxableCategoryDto taxableCategory;
}