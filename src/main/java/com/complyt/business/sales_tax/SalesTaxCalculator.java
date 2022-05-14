package com.complyt.business.sales_tax;

import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SalesTaxCalculator {
    public SalesTax calculate(SalesTaxRate salesTaxRate, List<Item> items){
        Optional<Float> amount = items.stream()
                .map(item -> salesTaxRate.getTaxRate() * item.getUnitPrice() * item.getQuantity())
                .reduce(Float::sum);

        return new SalesTax(salesTaxRate, amount.get());
    }
}