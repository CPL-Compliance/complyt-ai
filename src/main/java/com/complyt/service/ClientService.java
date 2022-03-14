package com.complyt.service;

import com.complyt.model.Client;
import com.complyt.model.Order;
import com.complyt.repository.ClientRepository;
import com.complyt.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    OrderRepository orderRepository;

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public void addOrderToClient(String name, Order order){
        clientRepository.addOrderToClient(name, order);
    }
}