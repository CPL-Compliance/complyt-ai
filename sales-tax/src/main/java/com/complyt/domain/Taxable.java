package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;

public interface Taxable {
    float calculateSalesTaxAmount();
    TaxableCategory getTaxableCategory();
    TangibleCategory getTangibleCategory();
    String getTaxCode();
    JurisdictionalSalesTaxRules getJurisdictionalSalesTaxRules();
    Taxable withSalesTaxRate(SalesTaxRate salesTaxRate);
}