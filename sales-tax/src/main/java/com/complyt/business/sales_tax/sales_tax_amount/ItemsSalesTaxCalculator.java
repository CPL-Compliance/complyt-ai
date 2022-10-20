package com.complyt.business.sales_tax.sales_tax_amount;

import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class ItemsSalesTaxCalculator {

    public float calculate(@NonNull List<Item> items) {
        log.info("Calculating total sales tax amount for items");
        float amount = 0;
        for (Item item : items) {
            amount += calculateSalesTaxAmount(item);
        }
        log.debug("Items Sales tax amount calculated : " + amount);

        return amount;
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