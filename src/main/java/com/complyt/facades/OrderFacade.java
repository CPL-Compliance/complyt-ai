package com.complyt.facades;

import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.services.OrderService;
import com.complyt.services.SalesTaxService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
@AllArgsConstructor
public class OrderFacade {
    @Qualifier("orderServiceImpl")
    @NonNull
    private OrderService orderService;

    @Qualifier("salesTaxServiceImpl")
    @NonNull
    private SalesTaxService salesTaxService;

    public Mono<Order> save(Order order) {
        return orderService.save(order);
    }

    public Mono<Order> upsert(Order order) {
        return orderService.upsert(order);
    }

    public Mono<Order> findByExternalId(String externalId) {
        return orderService.findByExternalId(externalId);
    }

    public Flux<Order> getAll() {
        return orderService.findAll();
    }

    @SneakyThrows
    public Order updateSalesTaxSync(String externalId) {
        Order order = orderService.findByExternalIdSync(externalId);
        SalesTax salesTax = salesTaxService.getSalesTaxSync(order.getShippingAddress(), order.getItems());
        Order orderWitSalesTax = order.withSalesTax(salesTax);

        return orderService.updateSync(orderWitSalesTax);
    }

    public Mono<Order> markAsCancelled(String orderId) {
        return orderService.markAsCancelled(orderId);
    }
}