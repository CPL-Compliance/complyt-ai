package com.complyt.facades;

import com.complyt.business.order.OrderJurisdictionalRulesInjector;
import com.complyt.domain.ClientTracking;
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
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

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

    public Mono<ClientTracking> getClientTracking() {
        return nexusService.getClientTracking();
    }

    public Mono<Order> upsert(@NonNull String externalId, Order order) {
        return orderService.upsert(externalId, order);
    }

    public Flux<Order> getOrdersByTimeFrame() {
        return nexusService.getOrdersByTimeFrame();
    }

    public Mono<Order> saveOrder(Order order) {
        return setBeforeSave(order)
                .flatMap(setOrder -> nexusService.hasNexus(setOrder)
                        .flatMap(hasNexus -> {
                            if (hasNexus == true) {
                                return calculateSalesTax(setOrder).flatMap(this::save);
                            } else {
                                return nexusService.handle(order).flatMap(this::save);
                            }
                        }));
    }
//    public Mono<Order> saveOrder(Order order) {
//        return setBeforeSave(order)
//                .flatMap(setOrder -> nexusService.hasNexus(setOrder)
//                        .flatMap(hasNexus -> {
//                            if (hasNexus == true) {
//                                return calculateSalesTax(setOrder).flatMap(this::save);
//                            } else {
//                                save(setOrder).subscribe(savedOrder -> {
//                                    log.info("******** " + savedOrder.toString());
//                                });
//                                //nexusService.handle(order);
//
//                                return Mono.just(order);
//                            }
//                        }));
//    }

    public Mono<Order> updateIfModified(@NonNull String externalId, Order order) {
        return findByExternalId(externalId)
                .flatMap(orderItem -> orderItem.equals(order) ?
                        Mono.just(order) :
                        setBeforeSave(order)
                                .flatMap(this::calculateSalesTax).log()
                                .flatMap(updatedOrder -> update(externalId, updatedOrder))
                );
    }

    public Mono<Order> setBeforeSave(Order order) {
        return productClassificationService
                .setJurisdictionalRules(new OrderJurisdictionalRulesInjector(order));
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