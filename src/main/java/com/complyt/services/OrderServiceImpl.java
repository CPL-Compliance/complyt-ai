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
    public void save(List<ObjectId> orders) {
        return;//orderRepository.insertAll(orders);
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Mono<Order> findOneByName(String name) {
        return orderRepository.findOneByName(name);
    }

    @Override
    public Flux<Order> findByName(String name) {
        return orderRepository.findByName(name);
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

    @Override
    public Flux<Order> findAll() {
        return null;
    }
}
