package com.complyt.services;

import com.complyt.domain.Order;
import com.complyt.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;

    @Override
    public void save(List<Order> orders) {
        orderRepository.insertAll(orders);
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order findOneByName(String name) {
        return orderRepository.findOneByName(name);
    }

    @Override
    public List<Order> findByName(String name) {
        return orderRepository.findByName(name);
    }

    @Override
    public Order findById(String id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findAll() {
        return null;
    }
}
