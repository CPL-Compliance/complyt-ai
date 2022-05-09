package com.complyt.services;

import com.complyt.domain.Order;
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
    public Mono<Order> findById(String id) {
        return orderRepository.findById(id);
    }

    public Mono<Order> upsert(@NonNull Order order){ return orderRepository.upsert(order);}

    public Flux<Order> findAll() {
        return orderRepository.findAll();
    }
}
