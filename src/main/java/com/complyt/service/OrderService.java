package com.complyt.service;

import com.complyt.model.Client;
import com.complyt.model.Order;
import com.complyt.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    public void save(List<Order> orders){
        orderRepository.insertAll(orders);
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }
}
