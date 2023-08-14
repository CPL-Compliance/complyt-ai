package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@EqualsAndHashCode
@ToString
@Slf4j
@With
@AllArgsConstructor
public class ShippingFee implements Taxable {
    private final boolean manualSalesTax;
    private final double manualSalesTaxRate;
    private final double totalPrice;
    private final JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    private final SalesTaxRates salesTaxRates;
    private final String taxCode;
    private final TaxableCategory taxableCategory;
    private final TangibleCategory tangibleCategory;

}
