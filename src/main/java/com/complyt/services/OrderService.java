package com.complyt.services;

import com.complyt.domain.Order;

import java.util.List;

public interface OrderService {
    void save(List<Order> orders);

    Order save(Order order);

    Order findById(String id);
}
