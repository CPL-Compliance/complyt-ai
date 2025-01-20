package com.complyt.domain;

import com.complyt.business.transaction.BigDecimalProcessor;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import com.complyt.domain.transaction.tax.GtRates;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    default BigDecimal calculateSalesTaxAmount() {
        BigDecimal taxRate = isManualSalesTax() ? getManualSalesTaxRate() : getTaxRates().getTaxRate();

        return getCalculatedTotal().multiply(taxRate);
    }

    default BigDecimal calculateInclusiveSalesTaxAmount() {
        BigDecimal taxRate = isManualSalesTax() ? getManualSalesTaxRate() :
                getTaxRates() != null ? getTaxRates().getTaxRate() : BigDecimal.ZERO;

        return BigDecimalProcessor.removeTrailingZeros(getCalculatedTotal().subtract(getCalculatedTotal().divide(BigDecimal.ONE.add(taxRate), 6, RoundingMode.HALF_UP)));
    }

    // if isTaxInclusive is true - We extract the sales tax from the calculated total
    default BigDecimal removeInclusiveSalesTax() {
        BigDecimal taxRate = isManualSalesTax() ? getManualSalesTaxRate() :
                getTaxRates() != null ? getTaxRates().getTaxRate() : BigDecimal.ZERO;

        return BigDecimalProcessor.removeTrailingZeros(getCalculatedTotal().divide(BigDecimal.ONE.add(taxRate), 6, RoundingMode.HALF_UP));
    }

    Taxable withTangibleCategory(TangibleCategory intangible);

    Taxable withTaxableCategory(TaxableCategory notTaxable);
}