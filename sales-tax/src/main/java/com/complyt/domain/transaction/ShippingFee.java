package com.complyt.domain.transaction;

import com.complyt.domain.TaxRates;
import com.complyt.domain.Taxable;
import com.complyt.domain.transaction.tax.GtRates;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import lombok.*;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode
@ToString
@With
@AllArgsConstructor
public class ShippingFee implements Taxable {
    private final boolean manualSalesTax;
    private final BigDecimal manualSalesTaxRate;
    private final BigDecimal totalPrice;
    private final JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    private final JurisdictionalTaxRules jurisdictionalTaxRules;
    private final SalesTaxRates salesTaxRates;
    private final GtRates gtRates;
    private final String taxCode;
    private final TaxableCategory taxableCategory;
    private final TangibleCategory tangibleCategory;
    private final BigDecimal calculatedTotal;

    public final BigDecimal getTotalPrice() {
        return totalPrice != null ?
                totalPrice :
                BigDecimal.ZERO;
    }

    @Override
    public final BigDecimal getCalculatedTotal() {
        return calculatedTotal == null ? BigDecimal.ZERO :
                calculatedTotal;
    }

    @Override
    public TaxRates getTaxRates() {
        return salesTaxRates != null ?
                salesTaxRates :
                gtRates;
    }

    @Override
    public final BigDecimal getManualSalesTaxRate() {
        return manualSalesTaxRate != null ? manualSalesTaxRate : BigDecimal.ZERO;
    }

}
