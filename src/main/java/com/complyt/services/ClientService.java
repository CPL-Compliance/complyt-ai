package com.complyt.services;

import com.complyt.domain.Order;
import com.complyt.v1.model.ClientDto;

public interface ClientService {
    ClientDto save(ClientDto clientDto);

    ClientDto findByName(String name);

    void addOrderToClient(String name, Order order);
}
