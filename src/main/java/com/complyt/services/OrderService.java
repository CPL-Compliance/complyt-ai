package com.complyt.services;

import com.complyt.domain.Order;
import lombok.NonNull;
import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderService extends CrudService<Order, String> {
    void save(List<ObjectId> orders);
    Mono<Order> findByExternalId(@NonNull final String externalId);
    Mono<Order> upsert(@NonNull final String externalId, @NonNull final Order order);
    Mono<Order> update(@NonNull final String externalId, @NonNull final Order order);
    Mono<Order> markAsCancelled(@NonNull final  String orderId);
    public Mono<Order> create(Order order);

    Mono<Order> calculate(Order orderTemp);
}