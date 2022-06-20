package com.complyt.business.sales_tax;

import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SalesTaxCalculator {
    public float calculate(List<Item> items) {
        Optional<Float> amount = items.stream().map(this::calculateSalesTaxAmount).reduce(Float::sum);

        return amount.get();
    }

    private Float calculateSalesTaxAmount(Item item) {
        if (item.isManualSalesTax()) {
            return item.getManualSalesTaxAmount();
        }

        float totalPrice = calculateTotalPrice(item);

        return item.getSalesTaxRate().getTaxRate() * totalPrice;
    }

    private float calculateTotalPrice(Item item) {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = item.getJurisdictionalSalesTaxRules();

        if (jurisdictionalSalesTaxRules.isCalculatedByPercentage()) {
            return jurisdictionalSalesTaxRules.getCalculationValue() * item.getTotalPrice();
        }

        return item.getTotalPrice();
    }
}