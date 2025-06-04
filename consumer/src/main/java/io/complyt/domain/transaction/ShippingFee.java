package io.complyt.domain.transaction;

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

}