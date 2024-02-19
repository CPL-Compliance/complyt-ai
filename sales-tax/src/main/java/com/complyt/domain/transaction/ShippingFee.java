package com.complyt.domain.transaction;

import com.complyt.domain.Discountable;
import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
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
    private final SalesTaxRates salesTaxRates;
    private final String taxCode;
    private final TaxableCategory taxableCategory;
    private final TangibleCategory tangibleCategory;
    private final BigDecimal calculatedTotal;

    public final BigDecimal getTotalPrice() {
        return totalPrice != null ?
        totalPrice:
        BigDecimal.ZERO; //todo: remove all instances
    }

    @Override
    public final BigDecimal getManualSalesTaxRate() {
        return manualSalesTaxRate != null ? manualSalesTaxRate : BigDecimal.ZERO;
    }

}
