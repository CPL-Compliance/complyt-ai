package com.complyt.business.sales_tax;

import com.complyt.business.sales_tax.checker.ItemsTaxableCheck;
import com.complyt.domain.Item;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class SalesTaxCalculator {

    @NonNull
    private final ItemsTaxableCheck itemsTaxableCheck;

    public float calculate(@NonNull List<Item> items, ShippingFee shippingFee) {
        log.info("Calculating total sales tax amount");

        Optional<Float> amount = items.stream().map(this::calculateSalesTaxAmountForItem).reduce(Float::sum);
        log.debug("Items Sales tax amount calculated : " + amount);

        float shippingFeeSalesTax = handleSalesTaxAmountCalculationForShippingFee(shippingFee, items);

        return amount.get() + shippingFeeSalesTax;
    }

    private float calculateSalesTaxAmountForItem(Item item) {
        if (item.isManualSalesTax()) {
            return item.getManualSalesTaxAmount();
        }

        float totalPrice = calculateTotalPriceForItem(item);

        return item.getSalesTaxRate().getTaxRate() * totalPrice;
    }

    private float calculateTotalPriceForItem(Item item) {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = item.getJurisdictionalSalesTaxRules();

        if (jurisdictionalSalesTaxRules.isCalculatedByPercentage()) {
            return item.getTotalPrice() * jurisdictionalSalesTaxRules.getCalculationValue();
        }

        return item.getTotalPrice();
    }

    private float handleSalesTaxAmountCalculationForShippingFee(ShippingFee shippingFee, List<Item> items) {
        if(shippingFee == null || !itemsTaxableCheck.hasTaxableItem(items)) {
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