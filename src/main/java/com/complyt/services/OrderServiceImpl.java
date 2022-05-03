package com.complyt.services;

import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;

    @Override
    public void save(@NonNull List<ObjectId> orders) {
        throw new UnsupportedOperationException("save isn't implemented yet");
    }

    @Override
    public Mono<Order> save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Flux<Order> findByName(String name) {
        throw new UnsupportedOperationException("findByName isn't implemented");
    }

    @Override
    public Mono<Order> findOneByName(String name) {
        throw new UnsupportedOperationException("findOneByName isn't implemented");
    }

    @Override
    public Mono<Order> findByExternalId(String externalId) {
        return orderRepository.findByExternalId(externalId);
    }

    @Override
    public Order findByExternalIdSync(@NonNull String externalId) {
        return orderRepository.findByExternalIdSync(externalId);
    }

    @Override
    public Mono<Order> findById(String id) {
        return orderRepository.findById(id);
    }

    public Mono<Order> upsert(@NonNull Order order) {
        return orderRepository.upsertSync(order);
    }

    public Mono<Order> update(@NonNull Order order) {
        return orderRepository.update(order);
    }

    public Order updateSync(@NonNull Order order) {
        return orderRepository.updateSync(order);
    }

    @Override
    public Mono<Order> markAsCancelled(String orderId) {
        Order order = orderRepository.findByExternalIdSync(orderId);
        Order cancelledOrder = order.withOrderStatus(OrderStatus.CANCELLED);

        return Mono.just(orderRepository.updateSync(cancelledOrder));
    }

    public Flux<Order> findAll() {
        return orderRepository.findAll();
    }
}
