package io.complyt.domain.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.complyt.domain.Discountable;
import io.complyt.domain.Taxable;
import io.complyt.domain.nexus.enums.TangibleCategory;
import io.complyt.domain.nexus.enums.TaxableCategory;
import io.complyt.domain.sales_tax.SalesTaxRates;
import io.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import io.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import io.complyt.domain.transaction.tax.GtRates;
import lombok.Data;
import lombok.With;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@With
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item implements Taxable, Discountable {

    private final BigDecimal unitPrice;
    private final BigDecimal quantity;
    private final BigDecimal totalPrice;
    private final BigDecimal calculatedTotal;
    private final String description;
    private final String name;
    private final String taxCode;
    private final JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    private final JurisdictionalTaxRules jurisdictionalTaxRules;
    private final SalesTaxRates salesTaxRates;
    private final GtRates gtRates;
    private final boolean manualSalesTax;
    private final BigDecimal manualSalesTaxRate;
    private final BigDecimal discount;
    private final BigDecimal relativeTransactionDiscount;
    private final TangibleCategory tangibleCategory;
    private final TaxableCategory taxableCategory;

    @JsonCreator
    public Item(
            @JsonProperty("unitPrice") BigDecimal unitPrice,
            @JsonProperty("quantity") BigDecimal quantity,
            @JsonProperty("totalPrice") BigDecimal totalPrice,
            @JsonProperty("calculatedTotal") BigDecimal calculatedTotal,
            @JsonProperty("description") String description,
            @JsonProperty("name") String name,
            @JsonProperty("taxCode") String taxCode,
            @JsonProperty("jurisdictionalSalesTaxRules") JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules,
            @JsonProperty("jurisdictionalTaxRules") JurisdictionalTaxRules jurisdictionalTaxRules,
            @JsonProperty("salesTaxRates") SalesTaxRates salesTaxRates,
            @JsonProperty("gtRates") GtRates gtRates,
            @JsonProperty("manualSalesTax") boolean manualSalesTax,
            @JsonProperty("manualSalesTaxRate") BigDecimal manualSalesTaxRate,
            @JsonProperty("discount") BigDecimal discount,
            @JsonProperty("relativeTransactionDiscount") BigDecimal relativeTransactionDiscount,
            @JsonProperty("tangibleCategory") TangibleCategory tangibleCategory,
            @JsonProperty("taxableCategory") TaxableCategory taxableCategory
    ) {
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.calculatedTotal = calculatedTotal;
        this.description = description;
        this.name = name;
        this.taxCode = taxCode;
        this.jurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules;
        this.jurisdictionalTaxRules = jurisdictionalTaxRules;
        this.salesTaxRates = salesTaxRates;
        this.gtRates = gtRates;
        this.manualSalesTax = manualSalesTax;
        this.manualSalesTaxRate = manualSalesTaxRate;
        this.discount = discount;
        this.relativeTransactionDiscount = relativeTransactionDiscount;
        this.tangibleCategory = tangibleCategory;
        this.taxableCategory = taxableCategory;
    }

    public final BigDecimal getTotalPrice() {
        return this.totalPrice != null ?
                this.totalPrice :
                this.getUnitPrice().multiply(this.getQuantity());
    }

    public final BigDecimal getCalculatedTotal() {
        return calculatedTotal == null ? BigDecimal.ZERO :
                calculatedTotal;
    }

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
