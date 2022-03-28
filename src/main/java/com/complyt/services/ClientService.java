package com.complyt.services;

import com.complyt.domain.Client;
import com.complyt.domain.Order;

public interface ClientService extends CrudService<Client, String> {
    void addOrderToClient(String name, Order order);
}
