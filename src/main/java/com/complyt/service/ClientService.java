package com.complyt.service;

import com.complyt.entity.Client;
import com.complyt.repository.ClientRepository;
import com.complyt.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ClientRepository clientRepository;

    public Client getClientByName(String name) {
        return clientRepository.findByName(name);
    }
}