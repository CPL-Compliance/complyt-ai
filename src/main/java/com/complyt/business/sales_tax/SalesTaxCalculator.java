package com.complyt.business.sales_tax;

import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.SalesTax;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SalesTaxCalculator {
    public SalesTax calculate(List<Item> items){

        Optional<Float> amount = items.stream()
                .map(item -> item.getSalesTaxRate().getTaxRate() * item.getUnitPrice() * item.getQuantity())
                .reduce(Float::sum);

        return new SalesTax(amount.get());
    }
}