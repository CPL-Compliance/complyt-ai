package com.complyt.domain.sales_tax;

import com.complyt.domain.Item;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class SalesTaxCalculator {
    public SalesTax calculate(SalesTaxRate salesTaxRate, List<Item> items){
        float taxRate = salesTaxRate.getTaxRate() , amount = 0;
        for(Item item : items){
            amount += taxRate * Float.parseFloat(item.getPrice()) * Float.parseFloat(item.getQuantity());
        }

        SalesTax salesTax = new SalesTax(salesTaxRate, amount);

        return salesTax;
    }
}