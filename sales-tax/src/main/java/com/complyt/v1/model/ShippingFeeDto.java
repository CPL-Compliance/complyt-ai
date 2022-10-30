package com.complyt.v1.model;

import com.complyt.domain.sales_tax.SalesTaxRate;
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
@Schema(name = "ShippingFee")
public class ShippingFeeDto {
    private final boolean manualSalesTax;
    private final float manualSalesTaxRate;
    private final float price;
    private final JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    private final SalesTaxRate salesTaxRate;
    private String taxCode;
    private final TaxableCategoryDto taxableCategory;
    private final TangibleCategoryDto tangibleCategory;
}