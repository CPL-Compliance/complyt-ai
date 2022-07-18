package com.complyt.services;

import com.complyt.domain.Order;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService extends CrudService<Order, String> {
    Mono<Order> findByExternalId(@NonNull final String externalId);
    Mono<Order> upsert(@NonNull final String externalId, @NonNull final Order order);
    Mono<Order> update(@NonNull final String externalId, @NonNull final Order order);
    Mono<Order> markAsCancelled(@NonNull final  String orderId);
    Flux<Order> getOrdersByFilter(@NonNull Query query);
    Mono<Order> handleSalesTaxCalculation(@NonNull Order order, @NonNull SalesTaxTracking salesTaxTracking);
    Mono<Order> calculate(@NonNull Order order);
}