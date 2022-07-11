package com.complyt.facades;

import com.complyt.business.order.OrderJurisdictionalRulesInjector;
import com.complyt.domain.Order;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusTracking;
import com.complyt.services.NexusService;
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

    @NonNull
    private NexusService nexusService;

    public Mono<NexusStateRule> handle(String state){
        return nexusService.findRuleByState(state);
    }

    public Mono<Order> upsert(@NonNull String externalId, Order order) {
        return orderService.upsert(externalId, order);
    }

    public Mono<Order> saveOrder(Order order) {
        return nexusService.handle(order);
//        return nexusService.handle(order);
//        return nexusService.hasNexus(order.getShippingAddress().getState()).flatMap(hasNexus -> {
//            hasNexus ?
//                    calculateSalesTax(order).flatMap(this::save) :
//                    save(order).flatMap(nexusService::handle)
//
//        });
        }

//    public Mono<Order> saveOrder(Order order) {
//        return calculateSalesTax(order)
//                .flatMap(this::save);
//    }

    public Mono<Order> updateIfModified(@NonNull String externalId, Order order) {
        return findByExternalId(externalId)
                .flatMap(orderItem -> orderItem.equals(order) ?
                        Mono.just(order) :
                        calculateSalesTax(order).log()
                                .flatMap(updatedOrder -> update(externalId, updatedOrder))
                );
    }

    private Mono<Order> calculateSalesTax(Order order) {
        return productClassificationService
                .setJurisdictionalRules(new OrderJurisdictionalRulesInjector(order))
                .flatMap(orderService::calculate).log();
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