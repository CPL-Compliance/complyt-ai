package com.complyt.business.sales_tax.sales_tax_amount;

import com.complyt.domain.Item;
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
public class ItemsSalesTaxCalculator {

    public float calculate(@NonNull List<Item> items) {
        log.info("Calculating total sales tax amount for items");

        Optional<Float> amount = items.stream().map(this::calculateSalesTaxAmount).reduce(Float::sum);
        log.debug("Items Sales tax amount calculated : " + amount);

        return amount.get();
    }

    private float calculateSalesTaxAmount(Item item) {
        if (item.isManualSalesTax()) {
            return item.getManualSalesTaxAmount();
        }

        float totalPrice = calculateTotalPrice(item);

        return item.getSalesTaxRate().getTaxRate() * totalPrice;
    }

    private float calculateTotalPrice(Item item) {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = item.getJurisdictionalSalesTaxRules();

        if (jurisdictionalSalesTaxRules.isCalculatedByPercentage()) {
            return item.getTotalPrice() * jurisdictionalSalesTaxRules.getCalculationValue();
        }

        return item.getTotalPrice();
    }

}