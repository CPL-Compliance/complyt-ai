package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;

import java.math.BigDecimal;

public interface Taxable {
    TaxableCategory getTaxableCategory();

    TangibleCategory getTangibleCategory();

    String getTaxCode();

    JurisdictionalSalesTaxRules getJurisdictionalSalesTaxRules();

    Taxable withSalesTaxRates(SalesTaxRates salesTaxRates);

    BigDecimal getTotalPrice();

    boolean isManualSalesTax();

    SalesTaxRates getSalesTaxRates();

    BigDecimal getManualSalesTaxRate();

    default BigDecimal getManualSalesTaxAmount() {
        return getManualSalesTaxRate().multiply(getTotalPrice());
    }

    default BigDecimal calculateSalesTaxAmount() {
        if (isManualSalesTax()) {
            return getManualSalesTaxAmount();
        }

        return getTotalPrice().multiply(getSalesTaxRates().taxRate());
    }

    Taxable withTangibleCategory(TangibleCategory intangible);

    Taxable withTaxableCategory(TaxableCategory notTaxable);
}