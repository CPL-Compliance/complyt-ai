package com.complyt.services;

import com.complyt.domain.Customer;
import com.complyt.domain.Order;
import lombok.NonNull;
import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderService extends CrudService<Order, String> {
    void save(List<ObjectId> orders);

    Mono<Order> findByExternalId(@NonNull String externalId);

    Mono<Order> upsert(@NonNull Order order);

    Mono<Order> update(@NonNull Order order);
}
