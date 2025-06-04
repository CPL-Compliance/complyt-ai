package io.complyt.domain.transaction;

import io.complyt.domain.Discountable;
import io.complyt.domain.Taxable;
import io.complyt.domain.nexus.enums.TangibleCategory;
import io.complyt.domain.nexus.enums.TaxableCategory;
import io.complyt.domain.sales_tax.SalesTaxRates;
import io.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import io.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import io.complyt.domain.transaction.tax.GtRates;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode
@ToString
@With
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Item implements Taxable, Discountable {
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private BigDecimal totalPrice;
    private BigDecimal calculatedTotal;
    private String description;
    private String name;
    private String taxCode;
    private JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    private JurisdictionalTaxRules jurisdictionalTaxRules;
    private SalesTaxRates salesTaxRates;
    private GtRates gtRates;
    private boolean manualSalesTax;
    private BigDecimal manualSalesTaxRate;
    private BigDecimal discount; // This is the item given discount
    private BigDecimal relativeTransactionDiscount; // This is the relative part of the transaction discount that should be applied to the item
    private TangibleCategory tangibleCategory;
    private TaxableCategory taxableCategory;

    public final BigDecimal getTotalPrice() {
        return this.totalPrice != null ?
                this.totalPrice :
                this.getUnitPrice().multiply(this.getQuantity());
    }

}