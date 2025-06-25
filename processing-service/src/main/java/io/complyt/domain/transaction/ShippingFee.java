package io.complyt.domain.transaction;

import io.complyt.domain.Taxable;
import io.complyt.domain.nexus.enums.TangibleCategory;
import io.complyt.domain.nexus.enums.TaxableCategory;
import io.complyt.domain.sales_tax.SalesTaxRates;
import io.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import io.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import io.complyt.domain.transaction.tax.GtRates;
import lombok.With;

import java.math.BigDecimal;

@With
public record ShippingFee(boolean manualSalesTax, BigDecimal manualSalesTaxRate, BigDecimal totalPrice,
                          JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules,
                          JurisdictionalTaxRules jurisdictionalTaxRules, SalesTaxRates salesTaxRates, GtRates gtRates,
                          String taxCode, TaxableCategory taxableCategory, TangibleCategory tangibleCategory,
                          BigDecimal calculatedTotal) implements Taxable {

    @Override
    public BigDecimal totalPrice() {
        return totalPrice != null ?
                totalPrice :
                BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculatedTotal() {
        return calculatedTotal == null ? BigDecimal.ZERO :
                calculatedTotal;
    }

    @Override
    public BigDecimal manualSalesTaxRate() {
        return manualSalesTaxRate != null ? manualSalesTaxRate : BigDecimal.ZERO;
    }

}
