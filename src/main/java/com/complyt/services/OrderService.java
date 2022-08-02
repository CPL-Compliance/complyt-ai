package com.complyt.services;

import com.complyt.domain.Order;
import com.complyt.services.crud.CrudService;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService extends CrudService<Order, String> {
    Mono<Order> findByExternalId(@NonNull final String externalId);
    Mono<Order> update(@NonNull final String externalId, @NonNull final Order order);
    Mono<Order> injectDataToModifiedOrder(@NonNull Order newOrder, @NonNull Order oldOrder);
    Mono<Order> injectDataToNewOrder(@NonNull Order order);
    Mono<Order> markAsCancelled(@NonNull final  String orderId);
    Flux<Order> getOrdersByQuery(@NonNull Query query);
}