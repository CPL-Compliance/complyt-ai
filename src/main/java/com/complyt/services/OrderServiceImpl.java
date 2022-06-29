package com.complyt.services;

import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    @NonNull
    private OrderRepository orderRepository;

    @Override
    public Mono<Order> save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Mono<Order> findByExternalId(@NonNull String externalId) {
        return orderRepository.findByExternalId(externalId);
    }

    @Override
    public Mono<Order> upsert(@NonNull String externalId, @NonNull Order order) {
        return orderRepository.findByExternalId(externalId)
                .switchIfEmpty(orderRepository.save(order))
                .map(createUpdateOrderFunction(order))
                .flatMap(orderRepository::save);
    }

    public Mono<Order> update(@NonNull final String externalId, @NonNull final Order order) {
            return orderRepository.findByExternalId(externalId)
                .switchIfEmpty(Mono.error(new NotFoundException("No Order with externalId" + externalId)))
                .map(createUpdateOrderFunction(order))
                .flatMap(orderRepository::save);
    }

    @Override
    public Mono<Order> findById(String id) {
        return orderRepository.findById(id);
    }

    @Override
    public Mono<Order> markAsCancelled(String externalId) {
        return orderRepository
                .findByExternalId(externalId)
                .map(order -> order.withOrderStatus(OrderStatus.CANCELLED))
                .flatMap(orderRepository::save);
    }

    public Flux<Order> findAll() {
        return orderRepository.find();
    }

    @Override
    public void save(@NonNull List<ObjectId> orders) {
        throw new UnsupportedOperationException("save isn't implemented yet");
    }

    @Override
    public Flux<Order> findByName(String name) {
        throw new UnsupportedOperationException("findByName isn't implemented");
    }

    @Override
    public Mono<Order> findOneByName(String name) {
        throw new UnsupportedOperationException("findOneByName isn't implemented");
    }

    private Function<Order, Order> createUpdateOrderFunction(@NonNull final Order order) {
        return orderInfo -> orderInfo.withExternalId(order.getExternalId())
                .withBillingAddress(order.getBillingAddress())
                .withShippingAddress(order.getShippingAddress())
                .withCustomerId(order.getCustomerId())
                .withItems(order.getItems())
                .withOrderStatus(order.getOrderStatus())
                .withSalesTax(order.getSalesTax());
    }
}
