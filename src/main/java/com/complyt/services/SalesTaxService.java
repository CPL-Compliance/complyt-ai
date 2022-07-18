package com.complyt.services;

import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SalesTaxService {
    float calculateSalesTaxAmount(List<Item> items);
    Mono<Order> calculate(Order order);
    Mono<Order> handleSalesTaxCalculation(@NonNull Order order, @NonNull SalesTaxTracking salesTaxTracking);
}
