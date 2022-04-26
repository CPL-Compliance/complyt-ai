package com.complyt.domain.sales_tax;

import com.complyt.domain.Item;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class SalesTaxCalculator {
    public SalesTax calculate(Mono<SalesTaxRate> salesTaxRate, List<Item> items){
        return null;
    }
}