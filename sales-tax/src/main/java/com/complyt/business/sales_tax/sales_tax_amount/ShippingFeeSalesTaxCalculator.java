package com.complyt.business.sales_tax.sales_tax_amount;

import com.complyt.business.sales_tax.checker.TaxableItemExistCheck;
import com.complyt.domain.Item;
import com.complyt.domain.ShippingFee;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class ShippingFeeSalesTaxCalculator {

    @NonNull
    private TaxableItemExistCheck taxableItemExistCheck;

    public float calculate(@NonNull ShippingFee shippingFee, @NonNull List<Item> items) {
        log.info("Calculating total sales tax amount for shipping fee");

        return handleSalesTaxAmountCalculationForShippingFee(shippingFee, items);
    }

    private float handleSalesTaxAmountCalculationForShippingFee(ShippingFee shippingFee, List<Item> items) {
        if (!taxableItemExistCheck.hasTaxableItem(items)) {
            log.debug("No sales tax for shipping fee");
            return 0;
        }

        if (shippingFee.isManualSalesTax()) {
            log.debug("Shipping fee Sales tax was set manually, amount : " + shippingFee.getManualSalesTaxAmount());
            return shippingFee.getManualSalesTaxAmount();
        }

        float amount = shippingFee.getSalesTaxRate().getTaxRate() * shippingFee.getPrice();
        log.debug("Shipping fee Sales tax amount calculated : " + amount);

        return amount;
    }
}
