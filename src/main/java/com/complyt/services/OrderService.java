package com.complyt.services;

import com.complyt.domain.Order;
import lombok.NonNull;
import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderService extends CrudService<Order, String> {
    void save(List<ObjectId> orders);
    Mono<Order> findByExternalId(@NonNull String externalId);
    Order findByExternalIdSync(@NonNull String externalId);
    Mono<Order> upsert(@NonNull String externalId, @NonNull Order order);
    Order updateSync(@NonNull Order order);
    Mono<Order> update(@NonNull Order order);
    Mono<Order> markAsCancelled(String orderId);
}
