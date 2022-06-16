package com.complyt.business.sales_tax;

import com.complyt.domain.Item;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SalesTaxCalculator {
    public float calculate(List<Item> items){

        Optional<Float> amount = items.stream()
                .map(item -> {
                    float salesTaxRate = item.isManualSalesTax() ? item.getManualSalesTaxRate() : item.getSalesTaxRate().getTaxRate();
                    return salesTaxRate * item.getUnitPrice() * item.getQuantity();
                })
                .reduce(Float::sum);

        return amount.get();
    }
}