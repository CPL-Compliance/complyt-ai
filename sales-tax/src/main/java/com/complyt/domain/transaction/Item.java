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
public class Item implements Taxable, Discountable {
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private BigDecimal totalPrice;
    private BigDecimal calculatedTotal;
    private String description;
    private String name;
    private String taxCode;
    private JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    private SalesTaxRates salesTaxRates;
    private boolean manualSalesTax;
    private BigDecimal manualSalesTaxRate;
    private BigDecimal discount;
    private TangibleCategory tangibleCategory;
    private TaxableCategory taxableCategory;

    public final BigDecimal getTotalPrice() {
        return this.totalPrice != null ?
                this.totalPrice :
                this.getUnitPrice().multiply(this.getQuantity());
    }

    @Override
    public final BigDecimal getCalculatedTotal() {
        return calculatedTotal == null ? BigDecimal.ZERO :
                calculatedTotal;
    }

    @Override
    public final BigDecimal getManualSalesTaxRate() {
        return manualSalesTaxRate != null ? manualSalesTaxRate : BigDecimal.ZERO;
    }

    public final BigDecimal getQuantity() {
        return quantity != null ? quantity : BigDecimal.ZERO;
    }

    public final BigDecimal getUnitPrice() {
        return unitPrice != null ? unitPrice : BigDecimal.ZERO;
    }
}