package com.complyt.services;

import com.complyt.domain.Order;

import java.util.List;

public interface OrderService extends CrudService<Order, String> {
    void save(List<Order> orders);
}
