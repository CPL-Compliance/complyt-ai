package com.complyt.service;

import com.complyt.domain.Client;
import com.complyt.domain.Order;
import com.complyt.repository.ClientRepository;
import com.complyt.v1.mapper.ClientMapper;
import com.complyt.v1.model.ClientDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ClientRepository clientRepository;

    public ClientDto save(ClientDto clientDto) {
        Client client = ClientMapper.INSTANCE.clientDtoToClient(clientDto);
        Client result = clientRepository.save(client);

        return ClientMapper.INSTANCE.clientToClientDto(client);
    }

    public ClientDto getClient(String name) {
        Client client = clientRepository.findOne(name);
        ClientDto clientDto = ClientMapper.INSTANCE.clientToClientDto(client);

        return clientDto;
    }

    public void addOrderToClient(String name, Order order) {
        clientRepository.addOrderToClient(name, order);
    }
}