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
    public Order findByName(String name) {
        return null;
    }

    @Override
    public Order findById(String id) {
        return orderRepository.findById(id);
    }
}
