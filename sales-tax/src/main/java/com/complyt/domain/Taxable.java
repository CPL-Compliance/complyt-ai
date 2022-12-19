package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;

public interface Taxable {
    TaxableCategory getTaxableCategory();

    TangibleCategory getTangibleCategory();

    String getTaxCode();

    JurisdictionalSalesTaxRules getJurisdictionalSalesTaxRules();

    Taxable withSalesTaxRate(SalesTaxRate salesTaxRate);

    float getTotalPrice();

    boolean isManualSalesTax();

    SalesTaxRate getSalesTaxRate();

    float getManualSalesTaxRate();

    default float getManualSalesTaxAmount() {
        return getManualSalesTaxRate() * getTotalPrice();
    }

    default float calculateSalesTaxAmount() {
        if (isManualSalesTax()) {
            return getManualSalesTaxAmount();
        }

        return getTotalPrice() * getSalesTaxRate().getTaxRate();
    }

}