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
public class ShippingFee implements Taxable {
    private final boolean manualSalesTax;
    private final float manualSalesTaxRate;
    private final float totalPrice;
    private final JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    private final SalesTaxRate salesTaxRate;
    private final String taxCode;
    private final TaxableCategory taxableCategory;
    private final TangibleCategory tangibleCategory;

    public float getManualSalesTaxAmount() {
        return manualSalesTaxRate * totalPrice;
    }

    @Override
    public float calculateSalesTaxAmount() {
        log.info("Calculating total sales tax amount for shipping fee");
        if (isManualSalesTax()) {
            log.debug("Shipping fee Sales tax was set manually, amount : " + getManualSalesTaxAmount());
            return getManualSalesTaxAmount();
        }

        return handleSalesTaxAmountCalculationForShippingFee();
    }

    private float handleSalesTaxAmountCalculationForShippingFee() {
        if (jurisdictionalSalesTaxRules.calculatedByPercentageCheck()) {
            return totalPrice * jurisdictionalSalesTaxRules.getCalculationValue() * salesTaxRate.getTaxRate();
        }

        float amount = salesTaxRate.getTaxRate() * totalPrice;
        log.debug("Shipping fee Sales tax amount calculated : " + amount);

        return amount;
    }
}
