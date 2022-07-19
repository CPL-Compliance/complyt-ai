package com.complyt.facades;

import com.complyt.domain.Order;
import com.complyt.services.OrderService;
import com.complyt.services.ProductClassificationService;
import com.complyt.services.SalesTaxService;
import com.complyt.services.nexus.NexusService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
@Component
public class OrderFacade {
    @NonNull
    @Qualifier("orderServiceImpl")
    private OrderService orderService;

    @NonNull
    @Qualifier("salesTaxServiceImpl")
    private SalesTaxService salesTaxService;

    @NonNull
    @Qualifier("productClassificationServiceImpl")
    private ProductClassificationService productClassificationService;

    @NonNull
    private NexusService nexusService;

    public Mono<Order> saveOrder(Order order) {
        return productClassificationService.getOrderWithRelevantProductClassificationData(order)
                .flatMap(setOrder -> nexusService.findTrackingByState(setOrder)
                        .flatMap(salesTaxTracking -> nexusService.hasNexus(salesTaxTracking) ?
                                salesTaxService.handleSalesTaxCalculation(setOrder, salesTaxTracking).flatMap(orderService::save) :
                                orderService.save(setOrder).flatMap(nexusService::calculate).thenReturn(setOrder)));
    }

    public Mono<Order> updateIfModified(@NonNull String externalId, Order order) {
        return findByExternalId(externalId)
                .flatMap(orderItem ->
                        orderItem.equals(order) ?
                                Mono.just(order) :
                                productClassificationService.getOrderWithRelevantProductClassificationData(order)
                                        .flatMap(salesTaxService::calculate)
                                        .flatMap(updatedOrder -> orderService.update(externalId, updatedOrder))
                );
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