package io.complyt.domain.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.complyt.domain.Taxable;
import io.complyt.domain.nexus.enums.TangibleCategory;
import io.complyt.domain.nexus.enums.TaxableCategory;
import io.complyt.domain.sales_tax.SalesTaxRates;
import io.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import io.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import io.complyt.domain.transaction.tax.GtRates;
import lombok.*;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode
@ToString
@With
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

    @JsonCreator
    public ShippingFee(
            @JsonProperty("manualSalesTax") boolean manualSalesTax,
            @JsonProperty("manualSalesTaxRate") BigDecimal manualSalesTaxRate,
            @JsonProperty("totalPrice") BigDecimal totalPrice,
            @JsonProperty("jurisdictionalSalesTaxRules") JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules,
            @JsonProperty("jurisdictionalTaxRules") JurisdictionalTaxRules jurisdictionalTaxRules,
            @JsonProperty("salesTaxRates") SalesTaxRates salesTaxRates,
            @JsonProperty("gtRates") GtRates gtRates,
            @JsonProperty("taxCode") String taxCode,
            @JsonProperty("taxableCategory") TaxableCategory taxableCategory,
            @JsonProperty("tangibleCategory") TangibleCategory tangibleCategory,
            @JsonProperty("calculatedTotal") BigDecimal calculatedTotal
    ) {
        this.manualSalesTax = manualSalesTax;
        this.manualSalesTaxRate = manualSalesTaxRate;
        this.totalPrice = totalPrice;
        this.jurisdictionalSalesTaxRules = jurisdictionalSalesTaxRules;
        this.jurisdictionalTaxRules = jurisdictionalTaxRules;
        this.salesTaxRates = salesTaxRates;
        this.gtRates = gtRates;
        this.taxCode = taxCode;
        this.taxableCategory = taxableCategory;
        this.tangibleCategory = tangibleCategory;
        this.calculatedTotal = calculatedTotal;
    }
}
