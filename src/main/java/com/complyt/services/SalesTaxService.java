package com.complyt.services;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SalesTaxService {
    float calculateSalesTaxAmount(List<Item> items);
    Mono<Order> calculate(Order order);
}
