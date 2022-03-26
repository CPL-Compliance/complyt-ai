package com.complyt.service;

import com.complyt.domain.Order;
import com.complyt.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private OrderRepository orderRepository;

    public void save(List<Order> orders){
        orderRepository.insertAll(orders);
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }
}
