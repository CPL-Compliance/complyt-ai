package com.complyt.domain;

import com.complyt.domain.transaction.tax.GtRates;
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

    Taxable withGtRates(GtRates gstRates);

    BigDecimal getCalculatedTotal();

    boolean isManualSalesTax();

    TaxRates getTaxRates();

    BigDecimal getManualSalesTaxRate();

    default BigDecimal getManualSalesTaxAmount() {
        return getCalculatedTotal().multiply((getManualSalesTaxRate()));
    }

    default BigDecimal calculateSalesTaxAmount() {
        if (isManualSalesTax()) {
            return getManualSalesTaxAmount();
        }

        return getCalculatedTotal().multiply(getTaxRates().getTaxRate());
    }

    Taxable withTangibleCategory(TangibleCategory intangible);

    Taxable withTaxableCategory(TaxableCategory notTaxable);
}