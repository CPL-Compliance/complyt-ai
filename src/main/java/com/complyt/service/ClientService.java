package com.complyt.service;

import com.complyt.model.Client;
import com.complyt.model.Order;
import com.complyt.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ClientRepository clientRepository;

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public Client getClient(String name){
        return clientRepository.getClient(name);
    }

    public void addOrderToClient(String name, Order order){
        clientRepository.addOrderToClient(name, order);
    }
}