package com.complyt.business.sales_tax.sales_tax_amount;

import com.complyt.domain.ShippingFee;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@EqualsAndHashCode
@Slf4j
public class ShippingFeeSalesTaxCalculator implements ISalesTaxCalculator<ShippingFee> {

    @NonNull
    ShippingFee shippingFee;

    public float calculate() {
        log.info("Calculating total sales tax amount for shipping fee");

        return handleSalesTaxAmountCalculationForShippingFee(shippingFee);
    }

    private float handleSalesTaxAmountCalculationForShippingFee(ShippingFee shippingFee) {

        if (shippingFee.isManualSalesTax()) {
            log.debug("Shipping fee Sales tax was set manually, amount : " + shippingFee.getManualSalesTaxAmount());
            return shippingFee.getManualSalesTaxAmount();
        }

        float amount = shippingFee.getSalesTaxRate().getTaxRate() * shippingFee.getPrice();
        log.debug("Shipping fee Sales tax amount calculated : " + amount);

        return amount;
    }
}
