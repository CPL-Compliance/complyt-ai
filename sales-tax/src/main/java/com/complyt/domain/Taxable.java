package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;

public interface Taxable {
    TaxableCategory getTaxableCategory();

    TangibleCategory getTangibleCategory();

    String getTaxCode();

    JurisdictionalSalesTaxRules getJurisdictionalSalesTaxRules();

    Taxable withSalesTaxRates(SalesTaxRates salesTaxRates);

    float getTotalPrice();

    boolean isManualSalesTax();

    SalesTaxRates getSalesTaxRates();

    float getManualSalesTaxRate();

    default float getManualSalesTaxAmount() {
        return getManualSalesTaxRate() * getTotalPrice();
    }

    default float calculateSalesTaxAmount() {
        if (isManualSalesTax()) {
            return getManualSalesTaxAmount();
        }

        return getTotalPrice() * getSalesTaxRates().taxRate();
    }

    Taxable withTangibleCategory(TangibleCategory intangible);

    Taxable withTaxableCategory(TaxableCategory notTaxable);
}