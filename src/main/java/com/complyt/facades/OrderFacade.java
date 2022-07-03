package com.complyt.facades;

import com.complyt.business.order.OrderProductClassificationInjector;
import com.complyt.domain.Order;
import com.complyt.services.OrderService;
import com.complyt.services.ProductClassificationService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class OrderFacade {
    @Qualifier("orderServiceImpl")
    @NonNull
    private OrderService orderService;

    @Qualifier("productClassificationServiceImpl")
    @NonNull
    private ProductClassificationService productClassificationService;

    public Mono<Order> create(Order order) {
        return orderService.create(order);
    }

    public Mono<Order> upsert(@NonNull String externalId, Order order) {
        return orderService.upsert(externalId, order);
    }

    public Mono<Order> save(Order order) {
        return orderService.save(order);
    }

    public Mono<Order> updateIfModified(@NonNull String externalId, Order order) {
        return findByExternalId(externalId)
                .flatMap(orderItem -> {
                    boolean isEqual = orderItem.equals(order);
                    System.out.println("isEqual = " + isEqual);
                    return isEqual ?
                            Mono.just(order) :
                            calculateSalesTax(order)
                                    .flatMap(updatedOrder -> update(externalId, updatedOrder));
                });
    }

    public Mono<Order> calculateSalesTax(Order order) {
        return productClassificationService
                .setJuresdictionalRules(new OrderProductClassificationInjector(order))
                .flatMap(orderService::calculate);
    }

    public Mono<Order> update(@NonNull String externalId, Order order) {
        return orderService.update(externalId, order);
    }

    public Mono<Order> findByExternalId(String externalId) {
        return orderService.findByExternalId(externalId);
    }

    public Flux<Order> getAll() {
        return orderService.findAll();
    }

    public Mono<Order> markAsCancelled(String orderId) {
        return orderService.markAsCancelled(orderId);
    }
}