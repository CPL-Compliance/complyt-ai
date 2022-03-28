package com.complyt.services;

import com.complyt.domain.Order;
import com.complyt.v1.model.ClientDto;

public interface ClientService extends CrudService<ClientDto, String> {
    void addOrderToClient(String name, Order order);
}
