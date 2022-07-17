package com.complyt.facades;

import com.complyt.business.order.OrderJurisdictionalRulesInjector;
import com.complyt.business.order.OrderTangibleCategoryInjector;
import com.complyt.domain.Order;
import com.complyt.services.OrderService;
import com.complyt.services.ProductClassificationService;
import com.complyt.services.nexus.NexusService;
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

    @NonNull
    private NexusService nexusService;

    public Mono<Order> upsert(@NonNull String externalId, Order order) {
        return orderService.upsert(externalId, order);
    }

    public Mono<Order> saveOrder(Order order) {
        return setBeforeSave(order)
                .flatMap(setOrder -> nexusService.hasNexus(setOrder)
                        .flatMap(hasNexus -> hasNexus ?
                                calculateSalesTax(setOrder).flatMap(this::save) :
                                save(setOrder).flatMap(nexusService::handle).map(setOrder::withNexusTracking)));
    }

    public Mono<Order> updateIfModified(@NonNull String externalId, Order order) {
        return findByExternalId(externalId)
                .flatMap(orderItem -> orderItem.equals(order) ?
                        Mono.just(order) :
                        setBeforeSave(order)
                                .flatMap(this::calculateSalesTax)
                                .flatMap(updatedOrder -> update(externalId, updatedOrder))
                );
    }

    public Mono<Order> setBeforeSave(Order order) {
        return productClassificationService
                .setDataToOrder(order);
    }

    private Mono<Order> calculateSalesTax(Order order) {
        return orderService.calculate(order).log();
    }

    public Mono<Order> save(Order order) {
        return orderService.save(order);
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