package com.complyt.services;

import com.complyt.domain.Order;
import org.bson.types.ObjectId;

import java.util.List;

public interface OrderService extends CrudService<Order, String> {
    void save(List<ObjectId> orders);
}
