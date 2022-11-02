package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@EqualsAndHashCode
@ToString
@Slf4j
@With
@AllArgsConstructor
public class ShippingFee implements ITaxAble {
    private final boolean manualSalesTax;
    private final float manualSalesTaxRate;
    private final float price;
    private final JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    private final SalesTaxRate salesTaxRate;
    private final String taxCode;
    private final TaxableCategory taxableCategory;
    private final TangibleCategory tangibleCategory;

    public float getManualSalesTaxAmount() {
        return manualSalesTaxRate * price;
    }

    @Override
    public float calculateSalesTaxAmount() {
        log.info("Calculating total sales tax amount for shipping fee");

        return handleSalesTaxAmountCalculationForShippingFee();
    }

    private float handleSalesTaxAmountCalculationForShippingFee() {
        if (isManualSalesTax()) {
            log.debug("Shipping fee Sales tax was set manually, amount : " + getManualSalesTaxAmount());
            return getManualSalesTaxAmount();
        }

        float amount = salesTaxRate.getTaxRate() * price;
        log.debug("Shipping fee Sales tax amount calculated : " + amount);

        return amount;
    }
}
