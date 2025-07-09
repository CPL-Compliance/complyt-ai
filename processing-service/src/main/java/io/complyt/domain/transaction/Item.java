package io.complyt.domain.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.complyt.domain.Discountable;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Item(
        BigDecimal unitPrice,
        BigDecimal quantity,
        BigDecimal totalPrice,
        BigDecimal calculatedTotal,
        String description,
        String name,
        String taxCode,
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules,
        JurisdictionalTaxRules jurisdictionalTaxRules,
        SalesTaxRates salesTaxRates,
        GtRates gtRates,
        boolean manualSalesTax,
        BigDecimal manualSalesTaxRate,
        BigDecimal discount,
        BigDecimal relativeTransactionDiscount,
        TangibleCategory tangibleCategory,
        TaxableCategory taxableCategory
) implements Taxable, Discountable {

    public BigDecimal getTotalPrice() {
        return totalPrice != null ? totalPrice : getUnitPrice().multiply(getQuantity());
    }

    public BigDecimal getCalculatedTotal() {
        return calculatedTotal != null ? calculatedTotal : BigDecimal.ZERO;
    }

    public BigDecimal getManualSalesTaxRate() {
        return manualSalesTaxRate != null ? manualSalesTaxRate : BigDecimal.ZERO;
    }

    public BigDecimal getQuantity() {
        return quantity != null ? quantity : BigDecimal.ZERO;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice != null ? unitPrice : BigDecimal.ZERO;
    }
}
